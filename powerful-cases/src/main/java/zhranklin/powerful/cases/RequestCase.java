package zhranklin.powerful.cases;

import com.fasterxml.jackson.annotation.JsonProperty;
import zhranklin.powerful.model.Instruction;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by 张武 at 2019/9/20
 */
public class RequestCase extends Instruction {

	private List<Instruction> trace;

	private String traceNodeTmpl = "{{env(APP)}}({{statusCode()}})";

	@JsonProperty(required = false)
	private String name;

	private String description;

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
			current.setThenReturn(String.format("%s -> {{resultBody()}}", traceNodeTmpl));
			prev.setTo(current);
			prev = current;
		}
		prev.setThenReturn(traceNodeTmpl);
		return trace.get(0);
	}

}
