package zhranklin.powerful.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class Instruction {
	private int times = 1;

	private String mode = "none";

	private String tell = "";

	private String by = "http";

	@JsonProperty(defaultValue = "{}")
	private Object to = "{}";

	private Map<String, String> withHeaders = new HashMap<>();

	private String thenReturns = "";

	// headers returned

	public String getTell() {
		return tell;
	}

	public void setTell(String tell) {
		this.tell = tell;
	}

	public Object getTo() {
		return to;
	}

	public void setTo(Object to) {
		this.to = to;
	}

	public Map<String, String> getWithHeaders() {
		return withHeaders;
	}

	public void setWithHeaders(Map<String, String> withHeaders) {
		this.withHeaders = withHeaders;
	}

	public String getThenReturns() {
		return thenReturns;
	}

	public void setThenReturns(String thenReturns) {
		this.thenReturns = thenReturns;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}
