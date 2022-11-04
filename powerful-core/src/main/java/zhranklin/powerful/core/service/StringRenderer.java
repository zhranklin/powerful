package zhranklin.powerful.core.service;

import zhranklin.powerful.model.RenderingContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by 张武 at 2019/9/10
 */
public class StringRenderer implements EnvironmentAware {
    private final Map<String, Function<RenderingContext, Function<List<String>, Object>>> exprFuncs = new HashMap<>();
    private static final Random rand = new Random();
    private static final Pattern EXPR_PATTERN = Pattern.compile("\\{\\{(.+?)}}");
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("([-a-zA-Z_]+)\\((.*)\\)");
    public Environment environment;

    public StringRenderer() {
        exprFuncs.put("randomDigits", context -> params -> {
            int size = Integer.parseInt(params.get(0));
            return IntStream.range(0, size)
                    .mapToObj(i -> "" + rand.nextInt(10))
                    .collect(Collectors.joining(""));
        });
        exprFuncs.put("randomInt", context -> params -> {
            int parSize = params.size();
            switch (parSize) {
                case 0:
                    return rand.nextInt();
                case 1:
                    return rand.nextInt(Integer.parseInt(params.get(0)));
                case 2:
                    int left = Integer.parseInt(params.get(0));
                    int right = Integer.parseInt(params.get(1));
                    if (left >= right) {
                        throw new IllegalArgumentException(String.format("randomInt range invalid: [%s, %s)", left, right));
                    }
                    return rand.nextInt();
                default:
                    throw new IllegalArgumentException(String.format("Illegal arguments for randomInt: %s", params));
            }
        });
        exprFuncs.put("p", context -> params -> System.getProperty(params.get(0)));
        exprFuncs.put("param", context -> params -> context.getParams().get(params.get(0)));
        exprFuncs.put("env", context -> params -> System.getenv(params.get(0)));
        exprFuncs.put("apollo", context -> params -> environment.getProperty(params.get(0)));
        exprFuncs.put("springEnv", context -> params -> environment.getProperty(params.get(0)));
        exprFuncs.put("path", context -> params -> context.getPath());
        exprFuncs.put("timestamp", context -> params -> {
            Date now = new Date();
            String pattern = params.isEmpty() ? "yyyy-MM-dd hh:mm:ss" : params.get(0);
            return new SimpleDateFormat(pattern).format(now);
        });
        exprFuncs.put("header", context -> params -> {
            List<String> result = new ArrayList<>();
            String reqHeader = context.getRequestHeaders().get(params.get(0));
            if (!StringUtils.isEmpty(reqHeader)) {
                result.add(reqHeader);
            }
            if (context.getResult() != null) {
                String value = context.getResult().responseHeaders.get(params.get(0));
                if (value != null) {
                    result.add(value);
                }
            }
            return String.join("|", result);
        });
        exprFuncs.put("result", context -> params -> "" + context.getResult());
        exprFuncs.put("resultBody", context -> params -> {
            if (context.getResult() != null) {
                return context.getResult().result;
            }
            return null;
        });
        exprFuncs.put("statusCode", context -> params -> {
            if (context.getResult() != null) {
                return context.getResult().status;
            }
            return null;
        });
        exprFuncs.put("invokeResult", context -> params -> "" + context.getInvokeResult());
        exprFuncs.put("delay", context -> params -> "" + context.getDelayMillis());
    }

    private String calc(String expr, RenderingContext context) {
        Matcher matcher = FUNCTION_PATTERN.matcher(expr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("'%s' is not a valid expression.", expr));
        }
        String functionName = matcher.group(1);
        String paramsStr = matcher.group(2);
        Function<RenderingContext, Function<List<String>, Object>> function = exprFuncs.get(functionName);
        if (function == null) {
            throw new IllegalArgumentException(String.format("function '%s' not found", functionName));
        }
        List<String> params = paramsStr.isEmpty() ? new ArrayList<>() : Arrays.asList(paramsStr.split(","));
        Object result = function.apply(context).apply(params);
        return result == null ? "" : "" + result;
    }

    public String render(String source, RenderingContext requestContext) {
        if (source == null) {
            return null;
        }
        Matcher matcher = EXPR_PATTERN.matcher(source);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(1);
            matcher.appendReplacement(sb, calc(expr, requestContext));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public void setEnvironment(@Nullable Environment environment) {
        this.environment = environment;
    }
}
