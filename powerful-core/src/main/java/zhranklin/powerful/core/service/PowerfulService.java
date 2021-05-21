package zhranklin.powerful.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flipkart.zjsonpatch.JsonPatch;
import zhranklin.powerful.core.cases.RequestCase;
import zhranklin.powerful.core.invoker.RemoteInvoker;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by 张武 at 2019/9/6
 */
public class PowerfulService {

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(PowerfulService.class);
    private static final Random rand = new Random();
    protected final StringRenderer stringRenderer;
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 200, 3, TimeUnit.MINUTES, new SynchronousQueue<>());

    private final AtomicInteger executeCount = new AtomicInteger(0);

    @Autowired
    private TestingMethodService testingMethodService;

    private final Map<String, RemoteInvoker> invokers = new HashMap<>();

    public PowerfulService(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    public Object execute(Instruction instruction, RenderingContext context) {
        if (instruction.getTimes() <= 1) {
            return executeSingle(instruction, context, false, 0, 0, 0).renderResult;
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
        context.setResult(result);
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
        String template = !StringUtils.isEmpty(instruction.getResponseFmt()) ? instruction.getResponseFmt() : "{{resultBody()}}";
        long requestStarts = 0;
        try {
            if (waitTimeMillis >= 5) {
                Thread.sleep(waitTimeMillis);
            }
            requestStarts = System.nanoTime();
            doExecuteSingle(processLoop(instruction, executed), context);
        } catch (Exception e) {
            if (handleException) {
                context.setResult(e.getMessage());
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
                String indexStr = path.replaceAll("^(/trace\\[(\\d+)])?.*", "$2");
                if (!indexStr.isEmpty()) {
                    int index = Integer.parseInt(indexStr);
                    path = Stream.generate(() -> "/to").limit(index).collect(Collectors.joining())
                        + path.replaceAll("^/trace\\[(\\d+)]", "");
                }
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
        executeCount.incrementAndGet();
        RemoteInvoker invoker = invokers.get(instruction.getBy());
        if (invoker == null) {
            throw new IllegalStateException(String.format("Protocol not supported in this instance: '%s'", instruction.getBy()));
        }
        if (context.getRequestHeaders() != null && !StringUtils.isEmpty(instruction.getPropagateHeaders())) {
            HashSet<String> propagateHeaders = new HashSet<>(Arrays.asList(instruction.getPropagateHeaders().split(",")));
            propagateHeaders.retainAll(context.getRequestHeaders().keySet());
            if (instruction.getHeaders() != null) {
                propagateHeaders.removeAll(instruction.getHeaders().keySet());
            }
            if (!propagateHeaders.isEmpty()) {
                if (instruction.getHeaders() == null) {
                    instruction.setHeaders(new HashMap<>());
                }
                propagateHeaders.forEach(headerName -> instruction.getHeaders().put(headerName, context.getRequestHeaders().get(headerName)));
            }
        }
        if (!StringUtils.isEmpty(instruction.getCall())) {
            context.setResult(invoker.invoke(instruction, context));
        }
        Integer tm = instruction.getCallTestMethod();
        if (tm != null) {
            context.setInvokeResult(invokeTestMethod(tm));
        }
        int delay = (int) (instruction.getDelay() * 1000);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Integer errorPercent = instruction.getErrorByPercent();
        if (errorPercent > 0) {
            if (rand.nextInt(100) < errorPercent) {
                logger.info("throw Random error");
                throw new RuntimeException("Random Error.");
            }
        }
    }

    public String invokeTestMethod(int n) {
        if (n == 0) {
            return null;
        }
        if (n > 100 || n < 0) {
            throw new IllegalArgumentException(String.format("invokeTestMethod: illegal n: %s", n));
        }
        try {
            int i = rand.nextInt(n);
            Method method = TestingMethodService.class.getMethod(String.format("method%02d", i));
            return (String) method.invoke(testingMethodService);
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
