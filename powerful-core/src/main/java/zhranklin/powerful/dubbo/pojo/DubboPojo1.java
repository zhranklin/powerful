package zhranklin.powerful.dubbo.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by 张武 at 2021/10/12
 */
public class DubboPojo1 implements Serializable {

	private String string;
	private Integer integer;

	@JsonProperty("double")
	private double aDouble;
	private DubboPojo2 pojo;

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public Integer getInteger() {
		return integer;
	}

	public void setInteger(Integer integer) {
		this.integer = integer;
	}

	public double getaDouble() {
		return aDouble;
	}

	public void setaDouble(double aDouble) {
		this.aDouble = aDouble;
	}

	public DubboPojo2 getPojo() {
		return pojo;
	}

	public void setPojo(DubboPojo2 pojo) {
		this.pojo = pojo;
	}

}
