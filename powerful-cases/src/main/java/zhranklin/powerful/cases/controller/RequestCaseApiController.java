package zhranklin.powerful.cases.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import zhranklin.powerful.cases.CaseValidator;
import zhranklin.powerful.cases.RequestCase;
import zhranklin.powerful.cases.StaticResources;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.service.PowerfulService;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/20
 */
@Controller
public class RequestCaseApiController {

    @Autowired
    private StaticResources staticResources;

    @Autowired
    PowerfulService powerful;

    @RequestMapping("/c")
    @ResponseBody
    Collection<String> cases() {
        return staticResources.rawCases.keySet();
    }

    @RequestMapping(value = "/", produces = "text/html")
    String casesHtml(ModelMap mm) {
        mm.put("cases", staticResources.rawCases.keySet());
        return "cases_page";
    }

    @RequestMapping("/c/{name}")
    @ResponseBody
    Object getCase(@PathVariable String name) throws JsonProcessingException {
        return staticResources.getRawCaseString(name);
    }

    @RequestMapping(value = "/e/{name}", method = RequestMethod.GET)
    @ResponseBody
    Object rCase(@PathVariable String name, @RequestParam Map<String, String> params) {
        RequestCase requestCase = staticResources.getCase(name);
        if (requestCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Instruction of name '%s' not found.", name));
        }
        return execute(requestCase, params);
    }


    @RequestMapping(value = "/e", method = RequestMethod.POST)
    @ResponseBody
    Object eCase(@RequestBody RequestCase requestCase, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) {
        Object result = execute(requestCase, params);
        if (validate) {
            Map<String, Object> ret = new HashMap<>();
            ret.put("result", result);
            ret.put("passed", CaseValidator.validate(requestCase.getExpect(), result));
            return ret;
        }
        return result;
    }

    private Object execute(RequestCase requestCase, Map<String, String> params) {
        Instruction instruction = requestCase.translateTrace();
        staticResources.processTargetMapping(instruction);
        RenderingContext context = new RenderingContext();
        context.setHttpParams(params);
        return powerful.execute(instruction, context);
    }
}
