package zhranklin.powerful.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class Echo {

	@JsonProperty(defaultValue = "")
	private String responseBody = "";

	@JsonProperty(defaultValue = "0")
	private double delay = 0;

	@JsonProperty(defaultValue = "200")
	private int statusCode = 200;

	@JsonProperty(defaultValue = "{}")
	private Map<String, String> responseHeaders = new HashMap<>();

	@JsonProperty(defaultValue = "false")
	private boolean showRequestInfo = false;

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public boolean isShowRequestInfo() {
		return showRequestInfo;
	}

	public void setShowRequestInfo(boolean showRequestInfo) {
		this.showRequestInfo = showRequestInfo;
	}
}
