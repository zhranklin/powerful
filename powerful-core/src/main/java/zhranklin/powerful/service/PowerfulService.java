package zhranklin.powerful.service;

import zhranklin.powerful.service.model.Instruction;
import zhranklin.powerful.service.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
    private final StringRenderer stringRenderer;
    private RestTemplate restTemplate;
    private AtomicInteger executeCount = new AtomicInteger(0);

    public PowerfulService(StringRenderer stringRenderer, RestTemplate restTemplate) {
        this.stringRenderer = stringRenderer;
        this.restTemplate = restTemplate;
    }

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

    public String executeSingle(Instruction instruction, RenderingContext context) {
        executeCount.incrementAndGet();
        if (!StringUtils.isEmpty(instruction.getTell())) {
            switch (instruction.getBy()) {
                case "http":
                    context.setResult(executeHttp(instruction, context));
                    break;
                default:
            }
        }
        int roundRobinNum = instruction.getThenOKTurnByRoundRobin();
        if (executeCount.get() % roundRobinNum != 0) {
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
                throw new RuntimeException("Random Error.");
            }
        }
        String template = !StringUtils.isEmpty(instruction.getThenReturn()) ? instruction.getThenReturn() : "{{resultBody()}}";
        return stringRenderer.render(template, context);
    }

    public ResponseEntity<String> executeHttp(Instruction instruction, RenderingContext context) {
        HttpHeaders headers = new HttpHeaders();
        instruction.getWithHeaders().forEach((k, v) -> {
            String value = stringRenderer.render(v, context);
            headers.set(k, value);
            logger.info("with header: " + k + " - " + value);
        });
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String url = stringRenderer.render(instruction.getTell() + "/execute", context);
        List<String> params = new ArrayList<>();
        instruction.getWithQuerys().forEach((k, v) -> params.add(k + "=" + stringRenderer.render(v, context)));
        if (params.size() != 0) {
            url = url + "?" + StringUtils.join(params, "&");
        }
        logger.info("post ：" + url);
        return restTemplate.exchange(new RequestEntity<>(instruction.getTo(), headers, HttpMethod.POST, URI.create(url)), String.class);
    }

}
