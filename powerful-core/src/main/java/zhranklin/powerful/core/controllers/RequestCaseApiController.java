package zhranklin.powerful.core.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

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
    Map<String, Collection<String>> cases() {
        return staticResources.rawCaseSets.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().keySet(), (i, j) -> i, LinkedHashMap::new));
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

    @RequestMapping(value = "/executeAll/{set}", method = RequestMethod.GET)
    void executeAll(@PathVariable String set, @RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        Set<String> names = staticResources.rawCaseSets.getOrDefault(set, Collections.emptyMap()).keySet();
        for (String name : names) {
            executeDuringResponse(params, response, writer, name);
        }
        writer.close();
    }

    @RequestMapping(value = "/executeAll", method = RequestMethod.GET)
    void executeAll(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        Set<String> names = staticResources.rawCases.keySet();
        for (String name : names) {
            executeDuringResponse(params, response, writer, name);
        }
        writer.close();
    }

    private void executeDuringResponse(Map<String, String> params, HttpServletResponse response, PrintWriter writer, String name) throws IOException {
        writer.println(String.format("=======executing: %s=======", name));
        response.flushBuffer();
        Object result = execute(staticResources.getCase(name), params, true);
        @SuppressWarnings("rawtypes") Boolean passed = (Boolean) ((Map) result).get("passed");
        if (passed) {
            writer.println("OK\n");
        } else {
            writer.println(String.format("!!!!!!!failed: %s!!!!!!!", name));
            writer.println("case:\n" + staticResources.getRawCaseString(name));
            writer.println("result: " + objectMapper.writeValueAsString(result));
        }
        response.flushBuffer();
    }

    @RequestMapping(value = "/e", method = RequestMethod.POST)
    @ResponseBody
    Object eCase(@RequestBody RequestCase requestCase, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) {
        return execute(requestCase, params, validate);
    }

    @RequestMapping(value = "/y", method = RequestMethod.POST)
    @ResponseBody
    Object yCase(HttpServletRequest request, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) throws IOException {
        String realBody = (String) request.getAttribute("realBody");
        return execute(new ObjectMapper(new YAMLFactory()).readValue(realBody, RequestCase.class), params, validate);
    }

    @RequestMapping(value = "/y/{body}", method = RequestMethod.GET)
    @ResponseBody
    Object yCase(@PathVariable String body, @RequestParam(required = false, defaultValue = "false") boolean validate, @RequestParam Map<String, String> params) throws IOException {
        String decodedBody = PowerfulService.decodeURLBase64(body);
        RequestCase requestCase = new ObjectMapper(new YAMLFactory()).readValue(decodedBody, RequestCase.class);
        return "----REQUEST----\n" +
            decodedBody +
            "\n\n---RESPONSE----\n" +
            new ObjectMapper(new YAMLFactory().configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false))
                .writeValueAsString(execute(requestCase, params, validate));
    }

    @RequestMapping(value = "/b", method = RequestMethod.POST)
    @ResponseBody
    String base64(HttpServletRequest request) {
        String realBody = (String) request.getAttribute("realBody");
        return PowerfulService.encodeURLBase64(realBody);
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
