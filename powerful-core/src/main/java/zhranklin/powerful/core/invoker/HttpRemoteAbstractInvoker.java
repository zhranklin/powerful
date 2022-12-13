package zhranklin.powerful.core.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerTraceNode;
import zhranklin.powerful.model.PowerfulResponse;
import zhranklin.powerful.model.RenderingContext;

import java.util.*;

public abstract class HttpRemoteAbstractInvoker implements RemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(PowerfulService.class);

    private final StringRenderer stringRenderer;

    protected HttpRemoteAbstractInvoker(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    private static final Set<String> METHODS_WITHOUT_BODY = Sets.newHashSet("GET", "DELETE");
    private static final Set<String> METHODS_WITH_BODY = Sets.newHashSet("POST", "PUT");

    @Override
    public PowerfulResponse invoke(Instruction instruction, RenderingContext context) {
        PowerTraceNode node = instruction.currentNode();

        // get headers
        final Map<String, String> headers = new HashMap<>(node.getHeaders());
        headers.put("Content-Type", "application/json;charset=UTF-8");

        // get url
        String url = node.getCall() + "/execute";
        List<String> params = new ArrayList<>();
        node.getQueries().forEach((k, v) -> params.add(k + "=" + stringRenderer.render(v, context)));
        if (!url.startsWith("http:")) {
            url = "http://" + url;
        }

        // get method
        String method = node.getMethod();

        try {
            // get body
            Instruction body;
            if (METHODS_WITH_BODY.contains(method)) {
                body = instruction;
            } else if (METHODS_WITHOUT_BODY.contains(method)) {
                body = null;
                // 将 instruction 作为请求参数
                params.add("_body=" + PowerfulService.encodeURLBase64(PowerfulService.jsonMapper.writeValueAsString(PowerfulService.getSimplifiedNode(instruction))));
            } else {
                throw new IllegalArgumentException(String.format("The method '%s' is not supported.", method));
            }
            // get params
            if (params.size() != 0) {
                url = url + "?" + StringUtils.join(params, "&");
            }
            if (instruction.isLog()) {
                String yamlBody = new ObjectMapper(new YAMLFactory()).writeValueAsString(PowerfulService.getSimplifiedNode(instruction));
                logger.info("{}:\n{}\nREQUEST:\n{}", method, url, yamlBody);
            }
            return doInvoke(headers, url, method, body);
        } catch (HttpServerErrorException | HttpClientErrorException e) {
            return new PowerfulResponse(e.getResponseBodyAsString(), "" + e.getRawStatusCode(), null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public abstract PowerfulResponse doInvoke(Map<String, String> headers, String url, String method, Instruction body);
}
