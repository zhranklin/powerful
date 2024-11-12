package zhranklin.powerful.model;

import feign.FeignException;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by 张武 at 2021/10/14
 */
public class PowerfulResponse {
	public static final String RESPONSE_BODY_METHOD = "responseBody";
	public static final String RESPONSE_HEADERS_METHOD = "responseHeaders";
	public static final String GET_MESSAGE_METHOD = "getMessage";
	public final Map<String, String> responseHeaders = new HashMap<>();
	public Object result;
	public String status;

	public PowerfulResponse() {
	}

	public PowerfulResponse(Object result, String status, Map<String, String> responseHeaders) {
		if (responseHeaders != null) {
			this.responseHeaders.putAll(responseHeaders);
		}
		this.result = result;
		this.status = status;
	}

	public static PowerfulResponse fromHttp(ResponseEntity<String> entity) {
		return new PowerfulResponse(entity.getBody(), ""+entity.getStatusCodeValue(), entity.getHeaders().toSingleValueMap());
	}

	public static PowerfulResponse fromHttp(FeignException e) {
		Method responseBodyMethod;
		Method responseHeadersMethod;
		try {
			responseBodyMethod = FeignException.class.getMethod(RESPONSE_BODY_METHOD);
			responseHeadersMethod = FeignException.class.getMethod(RESPONSE_HEADERS_METHOD);
			@SuppressWarnings("unchecked")
			Optional<ByteBuffer> bodyBuffer = (Optional<ByteBuffer>) responseBodyMethod.invoke(e);
			String body = "";
			if (bodyBuffer.isPresent()) {
				body = new String(bodyBuffer.get().array());
			}
			@SuppressWarnings("unchecked")
			Map<String, Collection<String>> headers = (Map<String, Collection<String>>) responseHeadersMethod.invoke(e);
			return new PowerfulResponse(body, "" + e.status(), toSingleValueMap(headers));
		} catch (NoSuchMethodException ex) {
			try {
				responseBodyMethod = FeignException.class.getMethod(GET_MESSAGE_METHOD);
				String body = (String) responseBodyMethod.invoke(e);
				return new PowerfulResponse(body, "" + e.status(), new HashMap<>());
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		} catch (InvocationTargetException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	private static Map<String, String> toSingleValueMap(Map<String, Collection<String>> stringCollectionMap) {
		Map<String, String> singleValueMap = new HashMap<>();
		for (Map.Entry<String, Collection<String>> entry : stringCollectionMap.entrySet()) {
			singleValueMap.put(entry.getKey(), entry.getValue().iterator().next());
		}
		return singleValueMap;
	}


	public static PowerfulResponse fromHttp(CloseableHttpResponse clientHttpResponse) throws IOException {
		Map<String, String> responseHeader = new HashMap<>();
		Arrays.asList(clientHttpResponse.getAllHeaders()).forEach(header -> responseHeader.put(header.getName(), header.getValue()));
		return new PowerfulResponse(EntityUtils.toString(clientHttpResponse.getEntity()),
				String.valueOf(clientHttpResponse.getStatusLine().getStatusCode()),
				responseHeader);
	}

	public static PowerfulResponse fromHttp(org.apache.hc.client5.http.impl.classic.CloseableHttpResponse clientHttpResponse) throws IOException, ParseException {
		Map<String, String> responseHeader = new HashMap<>();
		Header[] headers = clientHttpResponse.getHeaders();
		for (Header header : headers) {
			responseHeader.put(header.getName(), header.getValue());
		}
		return new PowerfulResponse(org.apache.hc.core5.http.io.entity.EntityUtils.toString(clientHttpResponse.getEntity()),
				String.valueOf(clientHttpResponse.getCode()),
				responseHeader);
	}

	public static PowerfulResponse fromHttp(ClientResponse clientResponse) {
		return new PowerfulResponse(clientResponse.bodyToMono(String.class).block(), String.valueOf(clientResponse.statusCode().value()), clientResponse.headers().asHttpHeaders().toSingleValueMap());
	}

	public ResponseEntity<String> makeHttpResponse(Instruction instruction) {
		HttpHeaders respHeaders = new HttpHeaders();
		instruction.currentNode().getResponseHeaders().forEach(respHeaders::set);
		//todo 翻译status
		return new ResponseEntity<>("" + result, respHeaders, HttpStatus.OK);
	}
}
