package zhranklin.powerful.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class RenderingContext implements Serializable{

	private Map<String, String> requestHeaders = new HashMap<>();
	private Object result = null;
	private String invokeResult;

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = (requestHeaders);
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getInvokeResult() {
		return invokeResult;
	}

	public void setInvokeResult(String invokeResult) {
		this.invokeResult = invokeResult;
	}
}
