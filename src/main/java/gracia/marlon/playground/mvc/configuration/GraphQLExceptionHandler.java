package gracia.marlon.playground.mvc.configuration;

import java.util.HashMap;
import java.util.Map;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import gracia.marlon.playground.mvc.exception.RestException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
		if (ex instanceof RestException) {
			RestException restException = (RestException) ex;

			Map<String, Object> extensions = new HashMap<>();
			extensions.put("message", restException.getMessage());
			extensions.put("code", restException.getError().getCode());
			extensions.put("httpCode", restException.getError().getHttpCode());

			ErrorType errorType = mapToErrorType(restException.getError().getHttpCode());

			return GraphqlErrorBuilder.newError().message(restException.getMessage()).errorType(errorType)
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation())
					.extensions(extensions).build();
		} else if (ex instanceof AccessDeniedException) {
			Map<String, Object> extensions = new HashMap<>();
			extensions.put("message", "Access denied: you do not have the required permissions.");
			extensions.put("code", "AUTH-0019");
			extensions.put("httpCode", 403);

			ErrorType errorType = mapToErrorType(403);

			return GraphqlErrorBuilder.newError().message("Access denied: you do not have the required permissions.")
					.errorType(errorType).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).extensions(extensions).build();
		}

		return super.resolveToSingleError(ex, env);
	}

	private ErrorType mapToErrorType(int httpCode) {
		switch (httpCode) {
		case 400:
			return ErrorType.BAD_REQUEST;
		case 401:
			return ErrorType.UNAUTHORIZED;
		case 403:
			return ErrorType.FORBIDDEN;
		case 404:
			return ErrorType.NOT_FOUND;
		case 500:
			return ErrorType.INTERNAL_ERROR;
		default:
			return ErrorType.INTERNAL_ERROR;
		}
	}
}