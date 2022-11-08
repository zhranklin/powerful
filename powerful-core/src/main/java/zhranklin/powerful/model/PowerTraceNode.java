package zhranklin.powerful.model;

import java.util.HashMap;
import java.util.Map;

public class PowerTraceNode {
	private String call = "";

	private String by = "http";

	private String method = "POST";

	private int code = 200;

	private Map<String, String> headers = new HashMap<>();

	private Map<String, String> responseHeaders = new HashMap<>();

	private Map<String, String> queries = new HashMap<>();

	private Integer callTestMethod = 0;

	private double delay = 0;

	private Integer errorByPercent = 0;

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		this.call = call;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, String> getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(Map<String, String> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public Map<String, String> getQueries() {
		return queries;
	}

	public void setQueries(Map<String, String> queries) {
		this.queries = queries;
	}

	public Integer getCallTestMethod() {
		return callTestMethod;
	}

	public void setCallTestMethod(Integer callTestMethod) {
		this.callTestMethod = callTestMethod;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public Integer getErrorByPercent() {
		return errorByPercent;
	}

	public void setErrorByPercent(Integer errorByPercent) {
		this.errorByPercent = errorByPercent;
	}
}
