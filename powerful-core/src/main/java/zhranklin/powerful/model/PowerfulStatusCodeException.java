package zhranklin.powerful.model;

import org.springframework.http.HttpStatus;

public class PowerfulStatusCodeException extends RuntimeException {
	public final HttpStatus status;

	public PowerfulStatusCodeException(HttpStatus status) {
		super("Custom status code: " + status);
		this.status = status;
	}

	public PowerfulStatusCodeException(int code) {
		this(HttpStatus.resolve(code));
		if (status == null) {
			throw new IllegalArgumentException("Status unsupported: " + code);
		}
	}

}
