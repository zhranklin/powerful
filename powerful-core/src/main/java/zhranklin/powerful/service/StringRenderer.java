package zhranklin.powerful.service;

import zhranklin.powerful.model.RenderingContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by 张武 at 2019/9/10
 */
public class StringRenderer implements EnvironmentAware {
    private Map<String, Function<RenderingContext, Function<List<String>, Object>>> exprFuncs = new HashMap<>();
    private static Random rand = new Random();
    private static Pattern EXPR_PATTERN = Pattern.compile("\\{\\{(.+?)}}");
    private static Pattern FUNCTION_PATTERN = Pattern.compile("([-a-zA-Z_]+)\\((.*)\\)");
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
        exprFuncs.put("apollo", context -> params -> environment.getProperty(params.get(0)));
        exprFuncs.put("timestamp", context -> params -> {
            Date now = new Date();
            String pattern = params.isEmpty() ? "yyyy-MM-dd hh:mm:ss" : params.get(0);
            return new SimpleDateFormat(pattern).format(now);
        });
        exprFuncs.put("receivedHeader", context -> params -> "" + context.getRequestHeaders().get(params.get(0)));
        exprFuncs.put("resultHeader", context -> params -> {
            if (context.getResult() instanceof ResponseEntity) {
                List<String> values = ((ResponseEntity) context.getResult()).getHeaders().get(params.get(0));
                return values == null ? "null" : String.join(",", values);
            } else {
                return null;
            }
        });
        exprFuncs.put("result", context -> params -> "" + context.getResult());
        exprFuncs.put("resultBody", context -> params -> {
            if (context.getResult() instanceof HttpEntity) {
                return "" + ((HttpEntity) context.getResult()).getBody();
            }
            return null;
        });
        exprFuncs.put("statusCode", context -> params -> {
            if (context.getResult() instanceof ResponseEntity) {
                return "" + ((ResponseEntity) context.getResult()).getStatusCodeValue();
            } else {
                return "null";
            }
        });
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
        return "" + function.apply(context).apply(params);
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
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
