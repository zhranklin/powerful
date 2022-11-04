package zhranklin.powerful.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class RenderingContext implements Serializable{

	private Map<String, String> requestHeaders = new HashMap<>();
	private String path = "";
	private Map<String, String> params = new HashMap<>();
	private String method = "";
	private final ThreadLocal<Result> results = new ThreadLocal<>();

	{
		results.set(new Result());
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = (requestHeaders);
	}

	public PowerfulResponse getResult() {
		return getResults().response;
	}

	public void setResult(PowerfulResponse result) {
		getResults().response = result;
	}

	public String getInvokeResult() {
		return getResults().invokeResult;
	}

	public void setInvokeResult(String invokeResult) {
		getResults().invokeResult = invokeResult;
	}

	public double getDelayMillis() {
		return getResults().delayMillis;
	}

	public void setDelayMillis(double delay) {
		getResults().delayMillis = delay;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	private Result getResults() {
		if (results.get() == null) {
			results.set(new Result());
		}
		return results.get();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private static class Result {
		private PowerfulResponse response = new PowerfulResponse();
		private String invokeResult;
		private double delayMillis;
	}

}
