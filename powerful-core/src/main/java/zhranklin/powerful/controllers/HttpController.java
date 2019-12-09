package zhranklin.powerful.controllers;

import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.service.PowerfulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = {"/**/execute"})
    public Object execute(@RequestBody Instruction instruction, HttpServletRequest request, @RequestParam Map<String, String> params) {
        try {
            RenderingContext context = new RenderingContext();
            context.setRequestHeaders(transformRequestHeaders(request));
            context.setHttpParams(params);
            Object result = powerful.execute(instruction, context);
            Object res = context.getResult();
            if (res instanceof ResponseEntity) {
                ResponseEntity<String> typed = (ResponseEntity<String>) res;
                return new ResponseEntity<>("" + result, typed.getStatusCode());
            }
            return result;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
