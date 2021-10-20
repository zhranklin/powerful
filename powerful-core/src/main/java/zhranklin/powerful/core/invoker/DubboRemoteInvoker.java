package zhranklin.powerful.core.invoker;

import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import zhranklin.powerful.assist.Gen;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;
import zhranklin.powerful.model.RenderingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by twogoods on 2019/10/29.
 */
public class DubboRemoteInvoker implements RemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(PowerfulService.class);

    private static ObjectMapper MAPPER = new ObjectMapper();
    private final StringRenderer stringRenderer;

    protected PowerfulService powerful;

    private Map<String, Class<?>> fieldToClass = new HashMap<>();

    public void setPowerful(PowerfulService powerful) {
        this.powerful = powerful;
    }

    public DubboRemoteInvoker(StringRenderer stringRenderer) {
        this.stringRenderer = stringRenderer;
    }

    @PostConstruct
    public void init() throws ClassNotFoundException {
        ClassLoader cl = getClass().getClassLoader();
        Gen.genDubboRefFields(fieldToClass);
    }

    @Override
    public PowerfulResponse invoke(Instruction instruction, RenderingContext context) {
        try {
            instruction.getHeaders().forEach(RpcContext.getContext()::setAttachment);
            if (instruction.isLog()) {
                String yamlBody = new ObjectMapper(new YAMLFactory()).writeValueAsString(PowerfulService.getSimplifiedNode(instruction, false));
                logger.info("DUBBO:\nREQUEST:\n{}", yamlBody);
            }
            RPCInvokeContext.renderingContext.set(context);
            try {
                Object result = invokeDubbo(instruction);
                logger.info("DEBUG#######################DEBUG########################################xxxxxxx has invoked: " + RpcContext.getContext().getAttachments().isEmpty());
                return new PowerfulResponse(result, "OK", RpcContext.getServerContext().getAttachments());
            } catch (Exception e) {
                e.printStackTrace();
                boolean hasInvoked = RpcContext.getContext().getAttachments().isEmpty();
                Map<String, String> responseHeaders = hasInvoked ? RpcContext.getServerContext().getAttachments() : null;
                return getPowerfulResponse(e, responseHeaders);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private PowerfulResponse getPowerfulResponse(Throwable e, Map<String, String> responseHeaders) {
        if (e instanceof InvocationTargetException && !(e.getCause() instanceof InvocationTargetException)) {
            return getPowerfulResponse(e.getCause(), responseHeaders);
        } else if (e instanceof RpcException) {
            String[] codeToStatus = new String[]{"UNKNOWN", "NETWORK", "TIMEOUT", "BIZ", "FORBIDDEN", "SERIALIZATION"};
            return new PowerfulResponse(e.getMessage(), "DUBBO_" + codeToStatus[((RpcException) e).getCode()], responseHeaders);
        } else {
            return new PowerfulResponse(e.getMessage(), e.getClass().getSimpleName(), responseHeaders);
        }
    }

    private Object invokeDubbo(Instruction instruction) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        String[] path = instruction.getCall().split("/");
        String app = path[0];
        String service = path[1];
        String methodName = path[2];

        String fieldName = Gen.genFieldName(app, service);
        Class<?> clazz = fieldToClass.get(fieldName);
        Object dubboService = DubboRemoteInvoker.class.getDeclaredField(fieldName).get(this);
        return dynamicInvoke(clazz, dubboService, methodName, instruction, Arrays.stream(path).skip(3).collect(Collectors.joining("/")));
    }

    public Object dynamicInvoke(Class<?> clazz, Object obj, String methodName, Instruction instruction, String paramsStr) throws InvocationTargetException, IllegalAccessException, IOException {
        List<String> params = new ArrayList<>();
        if (paramsStr.startsWith("[") && paramsStr.endsWith("]")) {
            for (Object o : MAPPER.readValue(paramsStr, List.class)) {
                params.add(MAPPER.writeValueAsString(o));
            }
        } else {
            params.addAll(Arrays.asList(paramsStr.split("/")));
        }

        Method method = Arrays.stream(clazz.getDeclaredMethods())
            .filter(m -> m.getName().equals(methodName) && Arrays.stream(m.getParameterTypes()).filter(c -> c != Instruction.class).count() == params.size())
            .findAny()
            .get();

        Object[] arguments = new Object[method.getParameterCount()];
        int paramIndex = 0;
        for (int i = 0; i < arguments.length; i++) {
            Class<?> type = method.getParameterTypes()[i];
            if (type == Instruction.class) {
                arguments[i] = instruction.getTo();
            } else {
                arguments[i] = unmarshalParam(params.get(paramIndex++), type);
            }
        }
        return method.invoke(obj, arguments);
    }

    private Object unmarshalParam(String s, Class<?> type) {
        try {
            return MAPPER.readValue(s, type);
        } catch (JsonParseException | JsonMappingException e) {
            if (type == String.class) {
                return s;
            } else {
                throw new IllegalStateException(e);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
