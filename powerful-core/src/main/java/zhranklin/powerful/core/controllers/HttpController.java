package zhranklin.powerful.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import reactor.core.publisher.Mono;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulStatusCodeException;
import zhranklin.powerful.model.RenderingContext;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
@RestController
public class HttpController {

    @Autowired
    private PowerfulService powerful;

    @RequestMapping(value = {"/**/execute"}, method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH})
    public Object execute(@RequestBody Instruction instruction, NativeWebRequest req, @RequestParam Map<String, String> params) {
        RenderingContext context = new RenderingContext();
        RequestAdaptor.HttpServletRequest request = new RequestAdaptor(req).getRequest();
        try {
            context.setMethod(request.getMethod());
            context.setRequestHeaders(transformRequestHeaders(request));
            context.setPath(request.getServletPath());
            context.setParams(params);
            powerful.execute(instruction, context);
			return wrapWebflux(req, context.getResult().makeHttpResponse(instruction));
        } catch (RuntimeException e) {
            e.printStackTrace();
            HttpHeaders respHeaders = new HttpHeaders();
            instruction.currentNode().getResponseHeaders().forEach(respHeaders::set);
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e.getCause() instanceof PowerfulStatusCodeException) {
                status = ((PowerfulStatusCodeException) e.getCause()).status;
            }
            if (e.getCause() instanceof FeignException) {
                int intStatus=((FeignException) e.getCause()).status();
                status = HttpStatus.valueOf(intStatus);
            }
			return wrapWebflux(req, new ResponseEntity<String>(e.getMessage(), respHeaders, status));
        }
    }
    @RequestMapping(value = {"/**/execute"}, method = {RequestMethod.GET, RequestMethod.DELETE})
    public Object execute(String _body, NativeWebRequest request, @RequestParam Map<String, String> params) throws IOException {
        Instruction instruction = new ObjectMapper().readValue(PowerfulService.decodeURLBase64(_body), Instruction.class);
        params.remove("_body");
		return wrapWebflux(request, execute(instruction, request, params));
    }

    private static Object wrapWebflux(NativeWebRequest req, Object result) {
        if ("true".equals(req.getHeader("powerful-is-webflux"))) {
            System.out.println("return webflux");
            return Mono.just(result);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> transformRequestHeaders(RequestAdaptor.HttpServletRequest request) {
        Enumeration<String> names = request.getHeaderNames();
        HashMap<String, String> result = new HashMap<>();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            result.put(name, String.join(",", CollectionUtils.toArray(values, new String[0])));
        }
        return result;
    }

}
