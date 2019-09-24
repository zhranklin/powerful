package zhranklin.powerful.service;

import zhranklin.powerful.service.model.BatchRedirect;
import zhranklin.powerful.service.model.Echo;
import zhranklin.powerful.service.model.Redirect;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

	public ResponseEntity<String> redirectHttp(Redirect redirect, RenderingContext context) {
		HttpHeaders headers = new HttpHeaders();
		redirect.getRequestHeaders().forEach((k, v) -> headers.set(k, stringRenderer.render(v, context)));
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		try {
			String url = stringRenderer.render(redirect.getUrl(), context);
			ResponseEntity<String> ret = restTemplate.exchange(new RequestEntity<>(redirect.getRequestBody(), headers, HttpMethod.POST, URI.create(url)), String.class);
			String respBody = ret.getBody();
			if (!StringUtils.isEmpty(redirect.getResponseBody())) {
				RenderingContext contextWithResp = new RenderingContext();
				contextWithResp.setRequestHeaders(context.getRequestHeaders());
				contextWithResp.setResponseBody(respBody);
				contextWithResp.setResponseHeaders(ret.getHeaders().toSingleValueMap());
				respBody = stringRenderer.render(redirect.getResponseBody(), contextWithResp);
			}
			return new ResponseEntity<>(respBody, ret.getHeaders(), ret.getStatusCode());
		} finally {
			context.setResponseHeaders(null);
		}
	}

	public Object batchRedirectHttp(BatchRedirect batchRedirect, RenderingContext context) {
		List<Redirect> requests = generateBatchRedirects(batchRedirect);
		Stream<ResponseEntity<String>> responses = requests.stream()
			.map(redirect -> redirectHttp(redirect, context));
		if ("list".equals(batchRedirect.getAggregationMode())) {
			return responses.map(ResponseEntity::getBody).collect(Collectors.toList());
		}
		return "";
	}

	private List<Redirect> generateBatchRedirects(BatchRedirect batchRedirect) {
		int totalTimes = batchRedirect.getTimes();
		int totalWeight = batchRedirect.getRedirects().stream().mapToInt(Redirect::getWeight).sum();
		if (!StringUtils.isEmpty(batchRedirect.getResponseBody())) {
			batchRedirect.getRedirects().forEach(r -> r.setResponseBody(batchRedirect.getResponseBody()));
		}
		ArrayList<Redirect> result = new ArrayList<>();
		batchRedirect.getRedirects()
			.forEach(redirect -> IntStream.range(0, redirect.getWeight() * totalTimes / totalWeight).forEach(i -> result.add(redirect)));
		if (result.size() < totalTimes) {
			IntStream.range(0, totalTimes - result.size())
				.forEach(i -> result.add(batchRedirect.getRedirects().get(rand.nextInt(result.size()))));
		}
		Collections.shuffle(result);
		return result;
	}
}
