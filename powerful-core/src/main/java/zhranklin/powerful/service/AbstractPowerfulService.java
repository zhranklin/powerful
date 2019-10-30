package zhranklin.powerful.service;

import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public AbstractPowerfulService(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    public abstract Object remoteCall(Instruction instruction, RenderingContext context);

    public Object execute(Instruction instruction, RenderingContext context) {
        if (instruction.getForTimes() <= 1 || "none".equals(instruction.getCollectBy())) {
            return executeSingle(instruction, context);
        }
        Stream<String> responses = IntStream.range(0, instruction.getForTimes())
                .mapToObj(i -> executeSingle(instruction, context));
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

    private String executeSingle(Instruction instruction, RenderingContext context) {
        executeCount.incrementAndGet();
        if (!StringUtils.isEmpty(instruction.getTell())) {
            context.setResult(remoteCall(instruction, context));
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
        String template = !StringUtils.isEmpty(instruction.getThenReturn()) ? instruction.getThenReturn() : "{{resultBody()}}";
        return stringRenderer.render(template, context);
    }


}
