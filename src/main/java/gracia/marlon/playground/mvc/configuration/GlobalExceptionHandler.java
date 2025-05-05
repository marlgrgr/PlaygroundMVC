package gracia.marlon.playground.mvc.configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import gracia.marlon.playground.mvc.exception.ApiError;
import gracia.marlon.playground.mvc.exception.RestException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(RestException.class)
	public ResponseEntity<ApiError> handleApiException(RestException ex) {
		log.error("An exception ocurred: " + ex.getError());
		final ApiError error = ex.getError();
		return ResponseEntity.status(error.getHttpCode()).body(error);
	}
}
