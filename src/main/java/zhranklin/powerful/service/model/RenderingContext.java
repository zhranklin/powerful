package zhranklin.powerful.service.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class RenderingContext {

	private Map<String, String> requestHeaders = new HashMap<>();
	private Map<String, String> responseHeaders = new HashMap<>();
	private Object responseBody = null;

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = (requestHeaders);
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public Object getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(Object responseBody) {
		this.responseBody = responseBody;
	}
}
