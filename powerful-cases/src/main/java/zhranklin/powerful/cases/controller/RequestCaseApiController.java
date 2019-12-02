package zhranklin.powerful.cases.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import zhranklin.powerful.cases.RequestCase;
import zhranklin.powerful.cases.StaticResources;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by 张武 at 2019/9/20
 */
@RequestMapping("/e2e")
@Controller
public class RequestCaseApiController {

    @Autowired
    private StaticResources staticResources;

    @Autowired
    PowerfulService powerful;

    @RequestMapping("/cases")
    @ResponseBody
    Collection<String> cases() {
        return staticResources.rawCases.keySet();
    }

    @RequestMapping(value = "/cases", produces = "text/html")
    String casesHtml(ModelMap mm) {
        mm.put("cases", staticResources.rawCases.keySet());
        return "cases_page";
    }

    @RequestMapping("/case/{name}")
    @ResponseBody
    Object getCase(@PathVariable String name) throws JsonProcessingException {
        return staticResources.getRawCaseString(name);
    }

    @RequestMapping(value = "/case/execute/{name}", method = RequestMethod.GET)
    @ResponseBody
    Object rCase(@PathVariable String name) {
        RequestCase requestCase = staticResources.getCase(name);
        if (requestCase == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Instruction of name '%s' not found.", name));
        }
        return execute(requestCase);
    }


    @RequestMapping(value = "/case/execute", method = RequestMethod.POST)
    @ResponseBody
    Object eCase(@RequestBody RequestCase requestCase) throws IOException {
        return execute(requestCase);
    }

    private Object execute(RequestCase requestCase) {
        staticResources.processTargetMapping(requestCase);
        return powerful.execute(requestCase, new RenderingContext());
    }
}
