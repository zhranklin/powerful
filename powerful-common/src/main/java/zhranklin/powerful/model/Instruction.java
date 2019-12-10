package zhranklin.powerful.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class Instruction implements Serializable {

	private String tell = "";

	private String by = "http";

	private String collectBy = "string";

	private int forTimes = 1;

	//@JsonProperty(defaultValue = "{}")
	private Instruction to;

	private Map<String, String> withHeaders = new HashMap<>();

	private Map<String, String> withQueries = new HashMap<>();

	private Integer thenCallTestMethod = 0;

	private double thenDelay = 0;

	private Integer thenThrowByPercent = 0;

	private Integer thenOKTurnByRoundRobin = 1;

	private String thenReturn = "";

	private String description;

	public String getTell() {
		return tell;
	}

	public void setTell(String tell) {
		this.tell = tell;
	}

	public Instruction getTo() {
		return to;
	}

	public void setTo(Instruction to) {
		this.to = to;
	}

	public Map<String, String> getWithHeaders() {
		return withHeaders;
	}

	public void setWithHeaders(Map<String, String> withHeaders) {
		this.withHeaders = withHeaders;
	}

	public String getThenReturn() {
		return thenReturn;
	}

	public void setThenReturn(String thenReturn) {
		this.thenReturn = thenReturn;
	}

	public int getForTimes() {
		return forTimes;
	}

	public void setForTimes(int forTimes) {
		this.forTimes = forTimes;
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

	public double getThenDelay() {
		return thenDelay;
	}

	public void setThenDelay(double thenDelay) {
		this.thenDelay = thenDelay;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getThenThrowByPercent() {
		return thenThrowByPercent;
	}

	public void setThenThrowByPercent(Integer thenThrowByPercent) {
		this.thenThrowByPercent = thenThrowByPercent;
	}

	public Integer getThenOKTurnByRoundRobin() {
		return thenOKTurnByRoundRobin;
	}

	public void setThenOKTurnByRoundRobin(Integer thenOKTurnByRoundRobin) {
		this.thenOKTurnByRoundRobin = thenOKTurnByRoundRobin;
	}

	public Map<String, String> getWithQueries() {
		return withQueries;
	}

	public void setWithQueries(Map<String, String> withQueries) {
		this.withQueries = withQueries;
	}

	public Integer getThenCallTestMethod() {
		return thenCallTestMethod;
	}

	public void setThenCallTestMethod(Integer thenCallTestMethod) {
		this.thenCallTestMethod = thenCallTestMethod;
	}
}
