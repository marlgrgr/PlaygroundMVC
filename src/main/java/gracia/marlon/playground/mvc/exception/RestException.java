package gracia.marlon.playground.mvc.exception;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ApiError error;

	public RestException(String message, String code, HttpStatus status) {
		super(message);
		this.error = new ApiError(message, code, status.value());
	}

	public ApiError getError() {
		return error;
	}

}
