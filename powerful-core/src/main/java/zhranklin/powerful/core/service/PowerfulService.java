package zhranklin.powerful.core.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.CompatibilityFlags;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import zhranklin.powerful.core.cases.RequestCase;
import zhranklin.powerful.core.invoker.RemoteInvoker;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerTraceNode;
import zhranklin.powerful.model.PowerfulResponse;
import zhranklin.powerful.model.PowerfulStatusCodeException;
import zhranklin.powerful.model.RenderingContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by 张武 at 2019/9/6
 */
public class PowerfulService {

    public static final ObjectMapper jsonMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger logger = LoggerFactory.getLogger(PowerfulService.class);
    private static final Random rand = new Random();
    protected final StringRenderer stringRenderer;
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 200, 3, TimeUnit.MINUTES, new SynchronousQueue<>());

    @Autowired
    private TestingMethodService testingMethodService;

    private final Map<String, RemoteInvoker> invokers = new HashMap<>();

    public PowerfulService(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    public static JsonNode getSimplifiedNode(Instruction instruction) throws IOException {
        if (instruction instanceof RequestCase) {
            instruction = jsonMapper.readValue(jsonMapper.writeValueAsString(instruction), Instruction.class);
        }
        ArrayNode trace = new ArrayNode(JsonNodeFactory.instance);
        for (PowerTraceNode t : instruction.getTrace()) {
            PowerTraceNode n = new PowerTraceNode();
            if (!t.getQueries().isEmpty()) {
                n.setQueries(null);
            }
            if (!t.getHeaders().isEmpty()) {
                n.setHeaders(null);
            }
            if (!t.getResponseHeaders().isEmpty()) {
                n.setResponseHeaders(null);
            }
            trace.add(simplify(n, t));
        }
        Instruction base = new Instruction();
        base.setTrace(null);
        List<PowerTraceNode> backup = instruction.getTrace();
        instruction.setTrace(null);
        JsonNode result = simplify(base, instruction);
        instruction.setTrace(backup);
        ((ObjectNode) result).put("trace", trace);
        return result;
    }

    private static JsonNode simplify(Object base, Object target) throws IOException {
        ArrayNode diff = (ArrayNode) JsonDiff.asJson(
            jsonMapper.readTree(jsonMapper.writeValueAsString(base)),
            jsonMapper.readTree(jsonMapper.writeValueAsString(target)), DiffFlags.dontNormalizeOpIntoMoveAndCopy().clone()
        );
        return JsonPatch.apply(
            diff,
            jsonMapper.readTree("{}"), EnumSet.of(CompatibilityFlags.ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE)
        );
    }

    public Object execute(Instruction instruction, RenderingContext context) {
        if (instruction.getTimes() <= 1) {
            String result = executeSingle(instruction, context, false, 0, 0, 0).renderResult;
            context.getResult().result = result;
            return result;
        }
        Stream<Result> responses = executeForTimes(instruction, context);
        Object result = null;
        String collectBy = instruction.getCollectBy();
        if ("list".equals(collectBy)) {
            result = responses.map(r -> r.renderResult).collect(Collectors.toList());
        } else if ("string".equals(collectBy)) {
            result = responses.map(r -> r.renderResult).collect(Collectors.joining("\n", "", "\n"));
        } else if (collectBy.startsWith("stat_")) {
            result = responses.collect(
                Collectors.groupingBy(r -> r.renderResult, TreeMap::new,
                    Collectors.collectingAndThen(Collectors.toList(), data -> {
                        String stat = collectBy.substring(5);
                        if (stat.equals("count")) {
                            return data.size();
                        } else if (stat.equals("avg")) {
                            return data.stream().mapToDouble(i -> i.delayMillis).sum() / data.size();
                        } else {
                            long quantile = Integer.parseInt(stat);
                            int index = Math.min(Math.max((int) (data.size() * quantile / 1000) - 1, 0), data.size()-1);
                            double[] doubles = data.stream().mapToDouble(i -> i.delayMillis).sorted().toArray();
                            System.out.println(Arrays.toString(doubles));
                            return doubles[index];
                        }
                    })
                )
            );
        }
        context.getResult().result = result;
        instruction.currentNode().getResponseHeaders().forEach((k, v) ->
            instruction.currentNode().getResponseHeaders().put(k, stringRenderer.render(v, context))
        );
        return result;
    }

    private Stream<Result> executeForTimes(Instruction instruction, RenderingContext context) {
        int totalTimes = instruction.getTimes();
        int threads = instruction.getThreads();
        long startTime = System.nanoTime();
        if (threads > 1) {
            return IntStream.range(0, threads)
                .mapToObj(i -> threadPool.submit(() -> {
                    int times = totalTimes / threads;
                    if (i < totalTimes % threads) {
                        times += 1;
                    }
                    return IntStream.range(0, times)
                        .mapToObj(j -> executeSingle(instruction, context, true, instruction.getQps() / (double)threads, startTime, j))
						.collect(Collectors.toList())
                        .stream();
                }))
				.collect(Collectors.toList())
                .stream()
                .flatMap(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        } else {
            return IntStream.range(0, totalTimes)
                .mapToObj(i -> executeSingle(instruction, context, true, instruction.getQps(), startTime, i));
        }
    }

    private Result executeSingle(Instruction instruction, RenderingContext context, boolean handleException, double qps, long startTime, int executed) {
        long waitTimeMillis = qps == 0 ? 0 : (long) (executed / qps * 1000) - (System.nanoTime() - startTime) / 1000000;
        String template = instruction.getTrace().size() < 2 ? instruction.getTraceNodeTmpl() : instruction.getTraceNodeTmpl() + " -> {{resultBody()}}";
        long requestStarts = 0;
        try {
            if (waitTimeMillis >= 5) {
                Thread.sleep(waitTimeMillis);
            }
            requestStarts = System.nanoTime();
            doExecuteSingle(processLoop(instruction, executed), context);
        } catch (Exception e) {
            if (handleException) {
                context.setResult(new PowerfulResponse(e.getMessage(), "200", null));
            } else {
                throw new RuntimeException(stringRenderer.render(template, context) + ": " + e.getMessage(), e);
            }
        } finally {
            double delayMillis = (System.nanoTime() - requestStarts) / 1000000f;
            context.setDelayMillis(delayMillis);
        }
        return new Result(context.getDelayMillis(), stringRenderer.render(template, context));
    }

    private Instruction processLoop(Instruction instruction, int i) {
    	if (instruction.getRr().isEmpty()) {
    	    return instruction;
        }
        try {
            ArrayNode patches = JsonNodeFactory.instance.arrayNode();
            for (Map.Entry<String, List<Object>> entry : instruction.getRr().entrySet()) {
                List<Object> values = entry.getValue();
                String path = "/" + entry.getKey().replaceAll("\\.", "/");
                ObjectNode patch = patches.addObject();
                patch.put("op", "replace");
                patch.put("path", path);
                Object value = values.get(i % values.size());
                patch.replace("value", jsonMapper.readTree(jsonMapper.writeValueAsString(value)));
            }
            return jsonMapper.readValue(jsonMapper.writeValueAsString(JsonPatch.apply(patches, jsonMapper.readTree(jsonMapper.writeValueAsString(instruction)))), RequestCase.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doExecuteSingle(Instruction instruction, RenderingContext context) {
        PowerTraceNode node = instruction.currentNode();
        propagateHeaders(instruction, context, node);
        renderHeaders(context);
        invokeTestMethod(context, node);
        delay(node);
        errorByPercent(node);
        code(node);
        call(instruction.getNext(), context);
    }

    private void renderHeaders(RenderingContext context) {
        if (context.getRequestHeaders() != null) {
            context.getRequestHeaders().forEach((k, v) -> {
                String value = stringRenderer.render(v, context);
                context.getRequestHeaders().put(k, value);
            });
        }
    }

    private void propagateHeaders(Instruction instruction, RenderingContext context, PowerTraceNode node) {
        if (context.getRequestHeaders() != null && !StringUtils.isEmpty(instruction.getPropagateHeaders())) {
            HashSet<String> propagateHeaders = new HashSet<>(Arrays.asList(instruction.getPropagateHeaders().split(",")));
            propagateHeaders.retainAll(context.getRequestHeaders().keySet());
            if (node.getHeaders() != null) {
                propagateHeaders.removeAll(node.getHeaders().keySet());
            }
            if (!propagateHeaders.isEmpty()) {
                if (node.getHeaders() == null) {
                    node.setHeaders(new HashMap<>());
                }
                propagateHeaders.forEach(headerName -> node.getHeaders().put(headerName, context.getRequestHeaders().get(headerName)));
            }
        }
    }

    private void call(Instruction instruction, RenderingContext context) {
        if (instruction == null || instruction.currentNode() == null) {
            return;
        }
        PowerTraceNode node = instruction.currentNode();
        if (node.getCall() != null && node.getCall().startsWith("dubbo://")) {
            node.setCall(node.getCall().substring("dubbo://".length()));
            node.setBy("dubbo");
        }
        RemoteInvoker invoker = invokers.get(node.getBy());
        if (invoker == null) {
            throw new IllegalStateException(String.format("Protocol not supported in this instance: '%s'", node.getBy()));
        }
        node.setCall(stringRenderer.render(node.getCall(), context));
        if (!StringUtils.isEmpty(node.getCall())) {
            context.setResult(invoker.invoke(instruction, context));
        }
    }

    private void code(PowerTraceNode node) {
        if (node.getCode() != 200) {
            throw new PowerfulStatusCodeException(node.getCode());
        }
    }

    private void errorByPercent(PowerTraceNode node) {
        Integer errorPercent = node.getErrorByPercent();
        if (errorPercent > 0) {
            if (rand.nextInt(100) < errorPercent) {
                logger.info("throw Random error");
                throw new RuntimeException("Random Error.");
            }
        }
    }

    private void delay(PowerTraceNode node) {
        int delay = (int) (node.getDelay() * 1000);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void invokeTestMethod(RenderingContext context, PowerTraceNode node) {
        Integer n = node.getCallTestMethod();
        if (n == null || n == 0) {
            return;
        }
        if (n > 100 || n < 0) {
            throw new IllegalArgumentException(String.format("invokeTestMethod: illegal n: %s", n));
        }
        try {
            int i = rand.nextInt(n);
            Method method = TestingMethodService.class.getMethod(String.format("method%02d", i));
            context.setInvokeResult((String) method.invoke(testingMethodService));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("SameReturnValue")
    private String fallback(Instruction instruction, RenderingContext context) {
        return "fallback";
    }

    public void setInvoker(String protocol, RemoteInvoker invoker) {
        if (invoker != null) {
            invokers.put(protocol, invoker);
        }
    }

    public static class Result {
        public final double delayMillis;
        public final String renderResult;

        public Result(double delayMillis, String renderResult) {
            this.delayMillis = delayMillis;
            this.renderResult = renderResult;
        }
    }

    public static String encodeURLBase64(String body) {
        return Base64Utils.encodeToUrlSafeString(body.trim().getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeURLBase64(String base64) {
        return new String(Base64Utils.decodeFromUrlSafeString(base64));
    }

}
