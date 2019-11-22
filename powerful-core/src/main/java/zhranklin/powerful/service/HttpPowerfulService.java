package zhranklin.powerful.service;

import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
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
public class HttpPowerfulService extends AbstractPowerfulService {

    private static Logger logger = LoggerFactory.getLogger(AbstractPowerfulService.class);
    private RestTemplate restTemplate;

    public HttpPowerfulService(StringRenderer stringRenderer, RestTemplate restTemplate) {
        super(stringRenderer);
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> remoteCall(Instruction instruction, RenderingContext context) {
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
