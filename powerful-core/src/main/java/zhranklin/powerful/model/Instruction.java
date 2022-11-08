package zhranklin.powerful.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张武 at 2019/9/6
 */
public class Instruction implements Serializable, Cloneable {

	private List<PowerTraceNode> trace = new ArrayList<>();

	private int times = 1;

	private int threads = 1;

	private int qps = 0;

	private String propagateHeaders = "";

	private static final String defaultCollectBy = System.getProperty("defaultCollectBy", "stat_count");
	private String collectBy = defaultCollectBy;

	private String traceNodeTmpl = defaultTraceNodeTmpl;
	public static final String defaultTraceNodeTmpl = System.getProperty("defaultTraceNodeTmpl", "{{env(APP)}}|{{env(VERSION)}}({{statusCode()}})");
	private boolean log = true;

	private Map<String, List<Object>> rr = new HashMap<>();

	public PowerTraceNode currentNode() {
		if (trace == null || trace.isEmpty()) {
			return null;
		}
		return trace.get(0);
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

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public String getPropagateHeaders() {
		return propagateHeaders;
	}

	public void setPropagateHeaders(String propagateHeaders) {
		this.propagateHeaders = propagateHeaders;
	}

	public int getQps() {
		return qps;
	}

	public void setQps(int qps) {
		this.qps = qps;
	}

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public Map<String, List<Object>> getRr() {
		return rr;
	}

	public void setRr(Map<String, List<Object>> rr) {
		this.rr = rr;
	}

	public List<PowerTraceNode> getTrace() {
		return trace;
	}

	public void setTrace(List<PowerTraceNode> trace) {
		this.trace = trace;
	}

	public String getTraceNodeTmpl() {
		return traceNodeTmpl;
	}

	public void setTraceNodeTmpl(String traceNodeTmpl) {
		this.traceNodeTmpl = traceNodeTmpl;
	}

	@JsonIgnore
	public Instruction getNext() {
		try {
			if (trace == null || trace.isEmpty()) {
				return null;
			}
			Instruction next = ((Instruction) this.clone());
			ArrayList<PowerTraceNode> nextTrace = new ArrayList<>(trace);
			nextTrace.remove(0);
			next.setTrace(nextTrace);
			next.times = 1;
			next.threads = 1;
			next.qps = 1;
			return next;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
