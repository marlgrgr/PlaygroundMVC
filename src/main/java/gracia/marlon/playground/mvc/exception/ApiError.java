package gracia.marlon.playground.mvc.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {

	private String message;

	private String code;

	private int httpCode;

}
