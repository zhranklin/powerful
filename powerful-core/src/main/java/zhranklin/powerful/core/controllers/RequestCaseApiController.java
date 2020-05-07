package zhranklin.powerful.core.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.core.cases.CaseValidator;
import zhranklin.powerful.core.cases.RequestCase;
import zhranklin.powerful.core.cases.StaticResources;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 张武 at 2019/9/20
 */
@Controller
public class RequestCaseApiController {

    @Autowired
    private StaticResources staticResources;

    @Autowired
    PowerfulService powerful;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("/c")
    @ResponseBody
    Collection<String> cases() {
        return staticResources.rawCases.keySet();
    }

    @RequestMapping(value = {"/", ""}, produces = "text/html")
    String casesHtml(ModelMap mm, @RequestParam(required = false) String scope) {
        mm.put("cases", staticResources.rawCases.keySet());
        if (scope == null) {
            return "forward:/index.html";
        } else {
            mm.put("scope", scope);
            return "scoped_index";
        }
    }

    @RequestMapping("/c/{name}")
    @ResponseBody
    Object getCase(@PathVariable String name) throws JsonProcessingException {
        return staticResources.getRawCaseString(name);
    }

    @RequestMapping(value = "/e/{name}", method = RequestMethod.GET)
    @ResponseBody
    Object rCase(@PathVariable String name, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) {
        RequestCase requestCase = staticResources.getCase(name);
        if (requestCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Instruction of name '%s' not found.", name));
        }
        return execute(requestCase, params, validate);
    }

    @RequestMapping(value = "/executeAll", method = RequestMethod.GET)
    void executeAll(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        Set<String> names = staticResources.rawCases.keySet();
        for (String name : names) {
            writer.println(String.format("=======executing: %s=======", name));
            response.flushBuffer();
            Object result = execute(staticResources.getCase(name), params, true);
            Boolean passed = (Boolean) ((Map) result).get("passed");
            if (passed) {
                writer.println("OK\n");
            } else {
                writer.println(String.format("!!!!!!!failed: %s!!!!!!!", name));
                writer.println("case:\n" + staticResources.getRawCaseString(name));
                writer.println("result: " + objectMapper.writeValueAsString(result));
            }
            response.flushBuffer();
        }
        writer.close();
    }

    @RequestMapping(value = "/e", method = RequestMethod.POST)
    @ResponseBody
    Object eCase(@RequestBody RequestCase requestCase, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) {
        return execute(requestCase, params, validate);
    }

    @RequestMapping(value = "/y", method = RequestMethod.POST)
    @ResponseBody
    Object yCase(HttpServletRequest request, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) throws IOException {
        return execute((RequestCase) request.getAttribute("yamlBody"), params, validate);
    }

    private Object execute(RequestCase requestCase, Map<String, String> params, boolean validate) {
        Instruction instruction = staticResources.processTargetMappingForTrace(requestCase).translateTrace();
        staticResources.processTargetMapping(instruction);
        RenderingContext context = new RenderingContext();
        context.setHttpParams(params);
        Object result = powerful.execute(instruction, context);
        if (validate) {
            Map<String, Object> ret = new HashMap<>();
            ret.put("result", result);
            ret.put("passed", CaseValidator.validate(requestCase.getExpect(), result));
            return ret;
        }
        return result;
    }
}
