package zhranklin.powerful.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class Instruction implements Serializable {

	private String call = "";

	private String by = "http";

	private static final String defaultCollectBy = System.getProperty("defaultCollectBy", "string");
	private String collectBy = defaultCollectBy;

	private int times = 1;

	private int threads = 1;

	//@JsonProperty(defaultValue = "{}")
	private Instruction to;

	private Map<String, String> headers = new HashMap<>();

	private Map<String, String> queries = new HashMap<>();

	private Integer callTestMethod = 0;

	private double delay = 0;

	private Integer errorByPercent = 0;

	private Integer okByRoundRobin = 1;

	private String responseFmt = "";

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		this.call = call;
	}

	public Instruction getTo() {
		return to;
	}

	public void setTo(Instruction to) {
		this.to = to;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getResponseFmt() {
		return responseFmt;
	}

	public void setResponseFmt(String responseFmt) {
		this.responseFmt = responseFmt;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getCollectBy() {
		return collectBy;
	}

	public void setCollectBy(String collectBy) {
		this.collectBy = collectBy;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
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

	public Integer getOkByRoundRobin() {
		return okByRoundRobin;
	}

	public void setOkByRoundRobin(Integer okByRoundRobin) {
		this.okByRoundRobin = okByRoundRobin;
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

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}
}
