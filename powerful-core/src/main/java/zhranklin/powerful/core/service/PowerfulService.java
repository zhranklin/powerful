package zhranklin.powerful.core.service;

import zhranklin.powerful.core.invoker.RemoteInvoker;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    private static Logger logger = LoggerFactory.getLogger(PowerfulService.class);
    private static Random rand = new Random();
    protected final StringRenderer stringRenderer;
    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 200, 3, TimeUnit.MINUTES, new SynchronousQueue<>());

    private AtomicInteger executeCount = new AtomicInteger(0);

    @Autowired
    private TestingMethodService testingMethodService;

    private Map<String, RemoteInvoker> invokers = new HashMap<>();

    public PowerfulService(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    public Object execute(Instruction instruction, RenderingContext context) {
        if (instruction.getTimes() <= 1) {
            return executeSingle(instruction, context, false);
        }
        Stream<String> responses = executeForTimes(instruction, context);
        Object result = null;
        if ("list".equals(instruction.getCollectBy())) {
            result = responses.collect(Collectors.toList());
        } else if ("string".equals(instruction.getCollectBy())) {
            result = responses.collect(Collectors.joining("\n", "", "\n"));
        } else if ("stat".equals(instruction.getCollectBy())) {
            result = responses.collect(Collectors.toMap(i -> i, i -> 1, Integer::sum));
        }
        context.setResult(result);
        return result;
    }

    private Stream<String> executeForTimes(Instruction instruction, RenderingContext context) {
        int totalTimes = instruction.getTimes();
        int threads = instruction.getThreads();
        if (threads > 1) {
            return IntStream.range(0, threads)
                .mapToObj(i -> threadPool.submit(() -> {
                    int times = totalTimes / threads;
                    if (i < totalTimes % threads) {
                        times += 1;
                    }
                    return IntStream.range(0, times)
                        .mapToObj(j -> executeSingle(instruction, context, true))
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
                .mapToObj(i -> executeSingle(instruction, context, true));
        }
    }

    private String executeSingle(Instruction instruction, RenderingContext context, boolean handleException) {
        try {
            doExecuteSingle(instruction, context);
        } catch (Exception e) {
            if (handleException) {
                context.setResult(e.getMessage());
            } else {
                throw e;
            }
        }
        String template = !StringUtils.isEmpty(instruction.getResponseFmt()) ? instruction.getResponseFmt() : "{{resultBody()}}";
        return stringRenderer.render(template, context);
    }

    private void doExecuteSingle(Instruction instruction, RenderingContext context) {
        executeCount.incrementAndGet();
        RemoteInvoker invoker = invokers.get(instruction.getBy());
        if (invoker == null) {
            throw new IllegalStateException(String.format("Protocol not supported in this instance: '%s'", instruction.getBy()));
        }
        if (!StringUtils.isEmpty(instruction.getCall())) {
            context.setResult(invoker.invoke(instruction, context));
        }
        Integer tm = instruction.getCallTestMethod();
        if (tm != null) {
            context.setInvokeResult(invokeTestMethod(tm));
        }
        int roundRobinNum = instruction.getOkByRoundRobin();
        if (executeCount.get() % roundRobinNum != 0) {
            logger.info("throw roundRobin error");
            throw new RuntimeException("roundRobin return error.");
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

    private String fallback(Instruction instruction, RenderingContext context) {
        return "fallback";
    }

    public void setInvoker(String protocol, RemoteInvoker invoker) {
        if (invoker != null) {
            invokers.put(protocol, invoker);
        }
    }

}
