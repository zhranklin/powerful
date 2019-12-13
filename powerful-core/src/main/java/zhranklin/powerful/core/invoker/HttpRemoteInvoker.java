package zhranklin.powerful.core.invoker;

import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by twogoods on 2019/10/29.
 */
public class HttpRemoteInvoker implements RemoteInvoker {

    private static Logger logger = LoggerFactory.getLogger(PowerfulService.class);
    private final StringRenderer stringRenderer;
    private RestTemplate restTemplate;

    public HttpRemoteInvoker(StringRenderer stringRenderer, RestTemplate restTemplate) {
        this.stringRenderer = stringRenderer;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> invoke(Instruction instruction, RenderingContext context) {
        HttpHeaders headers = new HttpHeaders();
        instruction.getHeaders().forEach((k, v) -> {
            String value = stringRenderer.render(v, context);
            headers.set(k, value);
            logger.info("with header: " + k + " - " + value);
        });
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String url = stringRenderer.render(instruction.getCall() + "/execute", context);
        List<String> params = new ArrayList<>();
        instruction.getQueries().forEach((k, v) -> params.add(k + "=" + stringRenderer.render(v, context)));
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        if (params.size() != 0) {
            url = url + "?" + StringUtils.join(params, "&");
        }
        logger.info("post ：" + url);
        try {
            return restTemplate.exchange(new RequestEntity<>(instruction.getTo(), headers, HttpMethod.POST, URI.create(url)), String.class);
        } catch (HttpServerErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

}
