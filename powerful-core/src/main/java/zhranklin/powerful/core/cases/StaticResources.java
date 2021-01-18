package zhranklin.powerful.core.cases;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import zhranklin.powerful.model.Instruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 张武 at 2019/9/20
 */
public class StaticResources {

    private static final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(StaticResources.class);

    public final Map<String, Object> rawCases = new LinkedHashMap<>();
    public final Properties targetMapping = new Properties();

    @SuppressWarnings("unchecked")
    public StaticResources(String configPath) {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        try {
            Resource resource = new File(configPath).exists() ?
                new FileSystemResource(configPath) : resourceResolver.getResources("classpath*:config.yaml")[0];
            //读取yaml配置, 例子见powerful-cases/config-example.yaml
            logger.info("use {}.", resource.getFilename());
            Map<String, Object> config = (Map<String, Object>) new ObjectMapper(new YAMLFactory()).readValue(resource.getInputStream(), Object.class);
            Map<String, Object> cases = (Map<String, Object>) config.get("requestCases");
            if (cases != null) {
                rawCases.putAll(cases);
            }
            Map<Object, Object> targetMappings = (Map<Object, Object>) config.get("targetMappings");
            if (targetMappings != null) {
                targetMapping.putAll(targetMappings);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public RequestCase getCase(String name) {
        Object obj = rawCases.get(name);
        if (obj == null) {
            return null;
        }
        RequestCase rCase;
        try {
            rCase = objectMapper.readValue(objectMapper.writeValueAsString(obj), RequestCase.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return rCase;
    }

    public String getRawCaseString(String name) throws JsonProcessingException {
        Object result = rawCases.get(name);
        if (result == null) {
            return "Not Found";
        } else {
            return objectMapper.writeValueAsString(result);
        }
    }

    public RequestCase processTargetMappingForTrace(RequestCase requestCase) {
        if (!CollectionUtils.isEmpty(requestCase.getTrace())) {
            requestCase.getTrace().forEach(t -> t.setCall(replaceTargets(t.getCall(), true)));
        }
        return requestCase;
    }
    public void processTargetMapping(Instruction instruction) {
        instruction.setCall(replaceTargets(instruction.getCall(), false));
        try {
            String to = replaceTargets(objectMapper.writeValueAsString(instruction.getTo()), false);
            instruction.setTo(objectMapper.readValue(to, Instruction.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String replaceTargets(String src, boolean trace) {
        Properties mapping = targetMapping;
        for (Enumeration<?> e = mapping.propertyNames(); e.hasMoreElements(); ) {
            String name = (String) e.nextElement();
            String value = mapping.getProperty(name);
            if (!StringUtils.isEmpty(value)) {
                if (trace) {
                    if (src.contains("/")) {
                        src = src.replaceAll("^" + name + "/", value + "/");
                    } else {
                        src = src.replaceAll("^" + name + "$", value);
                    }
                } else {
                    src = src.replaceAll("<" + name + ">", value);
                }
            }
        }
        return src;
    }

}
