package zhranklin.powerful.core.cases;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Created by 张武 at 2019/12/11
 */
public class CaseValidator {

	public static boolean validate(Object expect, Object result) {
		if (expect == null) {
			return true;
		}
		if (result == null) {
			result = "";
		}
		if (expect instanceof String || expect instanceof Number) {
			expect = expect.toString();
			if (result instanceof String) {
				return matchOrEquals((String) expect, (String) result);
			} else if (result instanceof Number) {
				return matchOrEquals((String) expect, result.toString()) || validateNumber((String) expect, ((Number) result));
			} else {
				throw new IllegalArgumentException(String.format("can't validate value for type '%s' and expr '%s'", result.getClass().getSimpleName(), expect));
			}
		} else if (expect instanceof Map) {
			if ("".equals(result)) {
				result = new HashMap<String, Object>();
			}
			if (!(result instanceof Map)) {
				return false;
			}
			Map<String, Object> resultMap = (Map<String, Object>) result;
			Map<String, Object> expectMap = (Map<String, Object>) expect;
			if (!expectMap.keySet().containsAll(resultMap.keySet())) {
				return false;
			}
			for (Map.Entry<String, Object> entry : expectMap.entrySet()) {
				Object resultValue = resultMap.get(entry.getKey());
				boolean valid = validate(entry.getValue(), resultValue);
				if (!valid) {
					return false;
				}
			}
			return true;
		} else {
			throw new IllegalStateException(String.format("Unknown type: %s", expect.getClass().getSimpleName()));
		}
	}

	private static boolean matchOrEquals(String regex, String str) {
		try {
			return regex.equals(str) || str.matches(regex);
		} catch(PatternSyntaxException e) {
			return false;
		}
	}

	private static boolean validateNumber(String expect, Number result) {
		if (expect.startsWith(":")) {
			double max = Double.parseDouble(expect.substring(1));
			return result.doubleValue() < max;
		} else if (expect.endsWith(":")) {
			double min = Double.parseDouble(expect.substring(0, expect.length() - 1));
			return result.doubleValue() > min;
		} else if (expect.contains(":")) {
			String[] range = expect.split(":");
			if (range.length != 2) {
				throw new IllegalArgumentException(String.format("not a number range: %s", expect));
			}
			double min = Double.parseDouble(range[0]);
			double max = Double.parseDouble(range[1]);
			return min <= result.doubleValue() && result.doubleValue() <= max;
		} else {
			double exact = Double.parseDouble(expect);
			return Math.abs(exact - result.doubleValue()) < 0.005;
		}
	}

}
