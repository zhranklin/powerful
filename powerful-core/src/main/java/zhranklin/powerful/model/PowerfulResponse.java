package zhranklin.powerful.model;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2021/10/14
 */
public class PowerfulResponse {
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

	public static PowerfulResponse fromHttp(CloseableHttpResponse clientHttpResponse) throws IOException {
		Map<String, String> responseHeader = new HashMap<>();
		Arrays.asList(clientHttpResponse.getAllHeaders()).forEach(header -> responseHeader.put(header.getName(), header.getValue()));
		return new PowerfulResponse(EntityUtils.toString(clientHttpResponse.getEntity()),
				String.valueOf(clientHttpResponse.getStatusLine().getStatusCode()),
				responseHeader);
	}

	public ResponseEntity<String> makeHttpResponse(Instruction instruction) {
		HttpHeaders respHeaders = new HttpHeaders();
		instruction.currentNode().getResponseHeaders().forEach(respHeaders::set);
		//todo 翻译status
		return new ResponseEntity<>("" + result, respHeaders, HttpStatus.OK);
	}
}
