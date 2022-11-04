package zhranklin.powerful.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = {"/**/execute"}, method = {RequestMethod.POST, RequestMethod.PUT})
    public Object execute(@RequestBody Instruction instruction, HttpServletRequest request, @RequestParam Map<String, String> params) {
        RenderingContext context = new RenderingContext();
        try {
            context.setMethod(request.getMethod());
            context.setRequestHeaders(transformRequestHeaders(request));
            context.setPath(request.getServletPath());
            context.setParams(params);
            powerful.execute(instruction, context);
            return context.getResult().makeHttpResponse(instruction);
        } catch (RuntimeException e) {
            e.printStackTrace();
            HttpHeaders respHeaders = new HttpHeaders();
            instruction.getResponseHeaders().forEach(respHeaders::set);
            return new ResponseEntity<>(e.getMessage(), respHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = {"/**/execute"}, method = {RequestMethod.GET, RequestMethod.DELETE})
    public Object execute(String _body, HttpServletRequest request, @RequestParam Map<String, String> params) throws IOException {
        Instruction instruction = new ObjectMapper().readValue(PowerfulService.decodeURLBase64(_body), Instruction.class);
        params.remove("_body");
        return execute(instruction, request, params);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> transformRequestHeaders(HttpServletRequest request) {
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
