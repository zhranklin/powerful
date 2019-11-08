package zhranklin.powerful.service;

import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by 张武 at 2019/9/6
 */
public abstract class AbstractPowerfulService extends EchoGrpc.EchoImplBase{

    private static Logger logger = LoggerFactory.getLogger(AbstractPowerfulService.class);
    private static Random rand = new Random();
    protected final StringRenderer stringRenderer;

    private AtomicInteger executeCount = new AtomicInteger(0);

    @Autowired private TestingMethodService testingMethodService;

    public AbstractPowerfulService(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    public abstract Object remoteCall(Instruction instruction, RenderingContext context);

    public Object execute(Instruction instruction, RenderingContext context) {
        if (instruction.getForTimes() <= 1 || "none".equals(instruction.getCollectBy())) {
            return executeSingle(instruction, context, false);
        }
        Stream<String> responses = IntStream.range(0, instruction.getForTimes())
                .mapToObj(i -> executeSingle(instruction, context, true));
        Object result = null;
        if ("list".equals(instruction.getCollectBy())) {
            result = responses.collect(Collectors.toList());
        } else if ("string".equals(instruction.getCollectBy())) {
            Optional res = responses.reduce((a, b) -> a + "\n" + b);
            result = res.get();
        }
        context.setResult(result);
        return result;
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
        String template = !StringUtils.isEmpty(instruction.getThenReturn()) ? instruction.getThenReturn() : "{{resultBody()}}";
        return stringRenderer.render(template, context);
    }

    private void doExecuteSingle(Instruction instruction, RenderingContext context) {
        executeCount.incrementAndGet();
        if (!StringUtils.isEmpty(instruction.getTell())) {
            context.setResult(remoteCall(instruction, context));
        }
        Integer tm = instruction.getThenCallTestMethod();
        if (tm != null) {
            context.setInvokeResult(invokeTestMethod(tm));
        }
        int roundRobinNum = instruction.getThenOKTurnByRoundRobin();
        if (executeCount.get() % roundRobinNum != 0) {
            logger.info("throw roundRobin error");
            throw new RuntimeException("roundRobin return error.");
        }
        int delay = (int) (instruction.getThenDelay() * 1000);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Integer errorPercent = instruction.getThenThrowByPercent();
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

}
