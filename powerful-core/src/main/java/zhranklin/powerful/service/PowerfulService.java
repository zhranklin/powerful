package zhranklin.powerful.service;

import zhranklin.powerful.service.model.Echo;
import zhranklin.powerful.service.model.Instruction;
import zhranklin.powerful.service.model.RenderingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by 张武 at 2019/9/6
 */
public class PowerfulService {

	private static Logger logger = LoggerFactory.getLogger(PowerfulService.class);
	private static Random rand = new Random();
	private final StringRenderer stringRenderer;
	private RestTemplate restTemplate;

	public PowerfulService(StringRenderer stringRenderer, RestTemplate restTemplate) {
		this.stringRenderer = stringRenderer;
		this.restTemplate = restTemplate;
	}

	public void echo(Echo echoRequest, RenderingContext requestContext) throws InterruptedException {
		echoRequest.setResponseBody(stringRenderer.render(echoRequest.getResponseBody(), requestContext));
		Map<String, String> headers = echoRequest.getResponseHeaders();
		headers.forEach((key, value) -> headers.put(key, stringRenderer.render(value, requestContext)));
		long delay = (long) (echoRequest.getDelay() * 1000);
		if (delay > 0) {
			Thread.sleep(delay);
		}
	}

	public ResponseEntity<String> redirectHttp(Instruction instruction, RenderingContext context) {
		HttpHeaders headers = new HttpHeaders();
		instruction.getWithHeaders().forEach((k, v) -> headers.set(k, stringRenderer.render(v, context)));
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		try {
			String url = stringRenderer.render(instruction.getTell(), context);
			ResponseEntity<String> ret = restTemplate.exchange(new RequestEntity<>(instruction.getTo(), headers, HttpMethod.POST, URI.create(url)), String.class);
			String respBody = ret.getBody();
			if (!StringUtils.isEmpty(instruction.getThenReturns())) {
				RenderingContext contextWithResp = new RenderingContext();
				contextWithResp.setRequestHeaders(context.getRequestHeaders());
				contextWithResp.setResponseBody(respBody);
				contextWithResp.setResponseHeaders(ret.getHeaders().toSingleValueMap());
				respBody = stringRenderer.render(instruction.getThenReturns(), contextWithResp);
			}
			return new ResponseEntity<>(respBody, ret.getHeaders(), ret.getStatusCode());
		} finally {
			context.setResponseHeaders(null);
		}
	}

	public Object batchRedirectHttp(Instruction instruction, RenderingContext context) {
		Stream<ResponseEntity<String>> responses = IntStream.range(0, instruction.getTimes())
			.mapToObj(i -> redirectHttp(instruction, context));
		if ("list".equals(instruction.getMode())) {
			return responses.map(ResponseEntity::getBody).collect(Collectors.toList());
		}
		return "";
	}

}
