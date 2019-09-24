package zhranklin.powerful.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张武 at 2019/9/6
 */
public class BatchRedirect {

	private int times = 1;

	@JsonProperty(defaultValue = "list")
	private String aggregationMode = "list";

	@JsonProperty(defaultValue = "")
	private String responseBody = "";

	@JsonProperty(defaultValue = "[]")
	private List<Redirect> redirects = new ArrayList<>();

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getAggregationMode() {
		return aggregationMode;
	}

	public void setAggregationMode(String aggregationMode) {
		this.aggregationMode = aggregationMode;
	}

	public List<Redirect> getRedirects() {
		return redirects;
	}

	public void setRedirects(List<Redirect> redirects) {
		this.redirects = redirects;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}
}
