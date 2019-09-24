package zhranklin.powerful.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.service.PowerfulService;
import zhranklin.powerful.service.model.BatchRedirect;
import zhranklin.powerful.service.model.Echo;
import zhranklin.powerful.service.model.Redirect;
import zhranklin.powerful.service.model.RenderingContext;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
@RestController
public class HttpController {

	private final PowerfulService powerfulService;
	private ObjectMapper mapper = new ObjectMapper();

	public HttpController(PowerfulService powerfulService) {
		this.powerfulService = powerfulService;
	}

	@RequestMapping("/echo")
	public String echo(@RequestBody(required = false) Echo echo, HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException {
		if (echo == null) {
			echo = new Echo();
			echo.setShowRequestInfo(true);
		}
		RenderingContext context = new RenderingContext();
		context.setRequestHeaders(transformRequestHeaders(request));
		powerfulService.echo(echo, context);
		String respBody;
		if (echo.isShowRequestInfo()) {
			EchoResponse result = wrapWithRequestInfo(request, echo);
			result.setResponse(echo.getResponseBody());
			respBody = mapper.writeValueAsString(result);
		} else {
			respBody = echo.getResponseBody();
		}
		response.setStatus(echo.getStatusCode());
		echo.getResponseHeaders().forEach(response::setHeader);
		response.setContentType(MappingJackson2JsonView.DEFAULT_CONTENT_TYPE);
		response.getWriter().write(respBody);
		return null;
	}

	@RequestMapping(value = "/redirect", params = "type=http")
	public String redirect(@RequestBody Redirect redirect, HttpServletRequest request, HttpServletResponse response) throws IOException {
		RenderingContext context = new RenderingContext();
		context.setRequestHeaders(transformRequestHeaders(request));
		ResponseEntity<String> responseEntity = powerfulService.redirectHttp(redirect, context);
		response.setStatus(responseEntity.getStatusCode().value());
		response.setContentType(MappingJackson2JsonView.DEFAULT_CONTENT_TYPE);
//		responseEntity.getHeaders().forEach((k, vs) -> response.setHeader(k, String.join("", vs)));
		if (responseEntity.getBody() != null) {
			response.getWriter().write(responseEntity.getBody());
		}
		return null;
	}

	@RequestMapping(value = "/batchRedirect", params = "type=http")
	public Object redirect(@RequestBody BatchRedirect batchRedirect, HttpServletRequest request) {
		RenderingContext context = new RenderingContext();
		context.setRequestHeaders(transformRequestHeaders(request));
		return powerfulService.batchRedirectHttp(batchRedirect, context);
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

	private EchoResponse wrapWithRequestInfo(HttpServletRequest request, Object requestBody) {
		EchoResponse hrb = new EchoResponse();
		EchoResponse.RequestInfo ri = new EchoResponse.RequestInfo();
		ri.setPath(request.getServletPath());
		ri.setRemoteAddress(request.getRemoteAddr());
		ri.setRequestBody(requestBody);
		ri.setRequestHeaders(transformRequestHeaders(request));
		hrb.setRequestInfo(ri);
		return hrb;
	}

}
