package zhranklin.powerful.core.cases;

import com.fasterxml.jackson.annotation.JsonProperty;
import zhranklin.powerful.model.Instruction;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 张武 at 2019/9/20
 */
public class RequestCase extends Instruction {

	private List<Instruction> trace;

	private String traceNodeTmpl = defaultTraceNodeTmpl;
	public static final String defaultTraceNodeTmpl = System.getProperty("defaultTraceNodeTmpl", "{{env(APP)}}({{statusCode()}})");

	@JsonProperty(required = false)
	private String name;

	private String description;

	private Object expect;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Instruction> getTrace() {
		return trace;
	}

	public void setTrace(List<Instruction> trace) {
		this.trace = trace;
	}

	public String getTraceNodeTmpl() {
		return traceNodeTmpl;
	}

	public void setTraceNodeTmpl(String traceNodeTmpl) {
		this.traceNodeTmpl = traceNodeTmpl;
	}

	public Instruction translateTrace() {
		if (CollectionUtils.isEmpty(trace)) {
			return this;
		}
		Instruction prev = this;
		for (Instruction current : trace) {
			prev.setCall(current.getCall());
			prev.setBy(current.getBy());
			prev.setHeaders(current.getHeaders());
			prev.setQueries(current.getQueries());
			prev.setTo(current);
			prev.setResponseFmt(String.format("%s -> {{resultBody()}}", traceNodeTmpl));
			current.setPropagateHeaders(getPropagateHeaders());
			prev = current;
		}
		prev.setCall("");
		prev.setHeaders(new HashMap<>());
		prev.setQueries(new HashMap<>());
		prev.setResponseFmt(traceNodeTmpl);
		return this;
	}

	public Object getExpect() {
		return expect;
	}

	public void setExpect(Object expect) {
		this.expect = expect;
	}
}
