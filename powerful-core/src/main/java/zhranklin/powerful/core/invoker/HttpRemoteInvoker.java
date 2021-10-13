package zhranklin.powerful.core.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Sets;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;
import zhranklin.powerful.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

/**
 * Created by twogoods on 2019/10/29.
 */
public class HttpRemoteInvoker implements RemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(PowerfulService.class);
    private final StringRenderer stringRenderer;
    private final RestTemplate restTemplate;
    private static final Set<String> METHODS_WITHOUT_BODY = Sets.newHashSet("GET", "DELETE");
    private static final Set<String> METHODS_WITH_BODY = Sets.newHashSet("POST", "PUT");

    public HttpRemoteInvoker(StringRenderer stringRenderer, RestTemplate restTemplate) {
        this.stringRenderer = stringRenderer;
        this.restTemplate = restTemplate;
    }

    public PowerfulResponse invoke(Instruction instruction, RenderingContext context) {
        HttpHeaders headers = new HttpHeaders();
        instruction.getHeaders().forEach(headers::set);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String url = instruction.getCall() + "/execute";
        List<String> params = new ArrayList<>();
        instruction.getQueries().forEach((k, v) -> params.add(k + "=" + stringRenderer.render(v, context)));
        if (!url.startsWith("http:")) {
            url = "http://" + url;
        }
        try {
            String method = instruction.getMethod();
            Instruction body;
            if (METHODS_WITH_BODY.contains(method)) {
                body = instruction.getTo();
            } else if (METHODS_WITHOUT_BODY.contains(method)) {
                body = null;
                params.add("_body=" + PowerfulService.encodeURLBase64(PowerfulService.jsonMapper.writeValueAsString(PowerfulService.getSimplifiedNode(instruction, true))));
            } else {
                throw new IllegalArgumentException(String.format("The method '%s' is not supported.", method));
            }
            if (params.size() != 0) {
                url = url + "?" + StringUtils.join(params, "&");
            }
            if (instruction.isLog()) {
                String yamlBody = new ObjectMapper(new YAMLFactory()).writeValueAsString(PowerfulService.getSimplifiedNode(instruction, false));
                logger.info("{}:\n{}\nREQUEST:\n{}", method, url, yamlBody);
            }
            return PowerfulResponse.fromHttp(restTemplate.exchange(new RequestEntity<>(body, headers, HttpMethod.valueOf(method), URI.create(url)), String.class));
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            return new PowerfulResponse(e.getResponseBodyAsString(), ""+e.getRawStatusCode(), null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
