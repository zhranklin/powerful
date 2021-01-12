package zhranklin.powerful.core.invoker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.flipkart.zjsonpatch.CompatibilityFlags;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.flipkart.zjsonpatch.JsonPatch;
import com.google.common.collect.Sets;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    private final ObjectMapper jsonMapper = new ObjectMapper();

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
                params.add("_body=" + PowerfulService.encodeURLBase64(jsonMapper.writeValueAsString(getSimplifiedNode(instruction, true))));
            } else {
                throw new IllegalArgumentException(String.format("The method '%s' is not supported.", method));
            }
            if (params.size() != 0) {
                url = url + "?" + StringUtils.join(params, "&");
            }
            if (instruction.isLog()) {
                String yamlBody = new ObjectMapper(new YAMLFactory()).writeValueAsString(getSimplifiedNode(instruction, false));
                logger.info("{}:\n{}\nREQUEST:\n{}", method, url, yamlBody);
            }
            return restTemplate.exchange(new RequestEntity<>(body, headers, HttpMethod.valueOf(method), URI.create(url)), String.class);
        } catch (HttpServerErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private JsonNode getSimplifiedNode(Instruction instruction, boolean responseFmt) throws IOException {
        Map<String, Object> blankObject = new LinkedHashMap<>();
        Instruction curInst = new Instruction();
        Instruction baseInstruction = curInst;
        Map<String, Object> curNode = blankObject;
        for (Instruction to = instruction.getTo(); to.getTo() != null; to = to.getTo()) {
            curInst.setTo(new Instruction());
            curInst = curInst.getTo();
            curNode.put("to", new HashMap<>());
            //noinspection unchecked
            curNode = (Map<String, Object>) curNode.get("to");
        }
        ArrayNode diff = (ArrayNode)JsonDiff.asJson(
            jsonMapper.readTree(jsonMapper.writeValueAsString(baseInstruction)),
            jsonMapper.readTree(jsonMapper.writeValueAsString(instruction.getTo())), DiffFlags.dontNormalizeOpIntoMoveAndCopy().clone()
        );
        String pathPrefix = "/to/";
        for (int i = 0; i < diff.size(); i++) {
            String path = diff.get(i).get("path").textValue();
            if (!responseFmt && path.endsWith("/responseFmt")) {
            	diff.remove(i--);
            	continue;
            }
            if (path.startsWith(pathPrefix)) {
                diff.insertObject(i)
                    .put("op", "replace")
                    .put("path", pathPrefix.replaceAll("/$", ""))
                    .putObject("value");
                pathPrefix += "to/";
            }
        }
        return JsonPatch.apply(
            diff,
            jsonMapper.readTree("{}"), EnumSet.of(CompatibilityFlags.ALLOW_MISSING_TARGET_OBJECT_ON_REPLACE)
        );
    }

}
