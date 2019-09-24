package zhranklin.powerful.controllers;

import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class EchoResponse {

	private RequestInfo requestInfo;

	private String response;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public RequestInfo getRequestInfo() {
		return requestInfo;
	}

	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}

	public static class RequestInfo {
		private Map<String, String> requestHeaders;

		private String path;

		private String remoteAddress;

		private Object requestBody;
		public Map<String, String> getRequestHeaders() {
			return requestHeaders;
		}

		public void setRequestHeaders(Map<String, String> requestHeaders) {
			this.requestHeaders = requestHeaders;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getRemoteAddress() {
			return remoteAddress;
		}

		public void setRemoteAddress(String remoteAddress) {
			this.remoteAddress = remoteAddress;
		}

		public Object getRequestBody() {
			return requestBody;
		}

		public void setRequestBody(Object requestBody) {
			this.requestBody = requestBody;
		}

	}

}
