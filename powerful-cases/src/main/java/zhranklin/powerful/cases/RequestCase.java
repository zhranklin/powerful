package zhranklin.powerful.cases;

import com.fasterxml.jackson.annotation.JsonProperty;
import zhranklin.powerful.model.Instruction;

/**
 * Created by 张武 at 2019/9/20
 */
public class RequestCase extends Instruction {

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
}