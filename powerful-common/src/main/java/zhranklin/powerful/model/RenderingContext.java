package zhranklin.powerful.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class RenderingContext implements Serializable{

	private Map<String, String> requestHeaders = new HashMap<>();
	private Map<String, String> httpParams = new HashMap<>();
	private ThreadLocal<Result> results = new ThreadLocal<>();

	{
		results.set(new Result());
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = (requestHeaders);
	}

	public Object getResult() {
		return getResults().result;
	}

	public void setResult(Object result) {
		getResults().result = result;
	}

	public String getInvokeResult() {
		return getResults().invokeResult;
	}

	public void setInvokeResult(String invokeResult) {
		getResults().invokeResult = invokeResult;
	}

	public Map<String, String> getHttpParams() {
		return httpParams;
	}

	public void setHttpParams(Map<String, String> httpParams) {
		this.httpParams = httpParams;
	}

	private Result getResults() {
		if (results.get() == null) {
			results.set(new Result());
		}
		return results.get();
	}

	private class Result {
		private Object result = null;
		private String invokeResult;
	}

}
