package zhranklin.powerful.dubbo.pojo;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by 张武 at 2021/10/12
 */
public class DubboPojo2 implements Serializable {
	private Map<String, String> props;

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}
}
