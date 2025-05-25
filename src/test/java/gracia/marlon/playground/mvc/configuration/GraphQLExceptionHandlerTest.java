package gracia.marlon.playground.mvc.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import gracia.marlon.playground.mvc.exception.RestException;
import graphql.GraphQLError;
import graphql.execution.ExecutionStepInfo;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;

public class GraphQLExceptionHandlerTest {

	@Test
	public void resolveToSingleError() throws Exception {
		GraphQLExceptionHandler graphQLExceptionHandler = new GraphQLExceptionHandler();
		RestException restException = new RestException("error BAD_REQUEST", "", HttpStatus.BAD_REQUEST);

		DataFetchingEnvironment dataFetchingEnvironment = Mockito.mock(DataFetchingEnvironment.class);
		ExecutionStepInfo executionStepInfo = Mockito.mock(ExecutionStepInfo.class);
		Field field = Mockito.mock(Field.class);

		Mockito.when(dataFetchingEnvironment.getExecutionStepInfo()).thenReturn(executionStepInfo);
		Mockito.when(executionStepInfo.getPath()).thenReturn(null);
		Mockito.when(dataFetchingEnvironment.getField()).thenReturn(field);
		Mockito.when(field.getSourceLocation()).thenReturn(null);

		GraphQLError graphQLError = graphQLExceptionHandler.resolveToSingleError(restException,
				dataFetchingEnvironment);
		assertEquals("error BAD_REQUEST", graphQLError.getMessage());
		assertEquals(ErrorType.BAD_REQUEST, graphQLError.getErrorType());

		restException = new RestException("error UNAUTHORIZED", "", HttpStatus.UNAUTHORIZED);
		graphQLError = graphQLExceptionHandler.resolveToSingleError(restException, dataFetchingEnvironment);
		assertEquals("error UNAUTHORIZED", graphQLError.getMessage());
		assertEquals(ErrorType.UNAUTHORIZED, graphQLError.getErrorType());

		restException = new RestException("error FORBIDDEN", "", HttpStatus.FORBIDDEN);
		graphQLError = graphQLExceptionHandler.resolveToSingleError(restException, dataFetchingEnvironment);
		assertEquals("error FORBIDDEN", graphQLError.getMessage());
		assertEquals(ErrorType.FORBIDDEN, graphQLError.getErrorType());

		restException = new RestException("error NOT_FOUND", "", HttpStatus.NOT_FOUND);
		graphQLError = graphQLExceptionHandler.resolveToSingleError(restException, dataFetchingEnvironment);
		assertEquals("error NOT_FOUND", graphQLError.getMessage());
		assertEquals(ErrorType.NOT_FOUND, graphQLError.getErrorType());

		restException = new RestException("error INTERNAL_ERROR", "", HttpStatus.INTERNAL_SERVER_ERROR);
		graphQLError = graphQLExceptionHandler.resolveToSingleError(restException, dataFetchingEnvironment);
		assertEquals("error INTERNAL_ERROR", graphQLError.getMessage());
		assertEquals(ErrorType.INTERNAL_ERROR, graphQLError.getErrorType());

		restException = new RestException("error BAD_GATEWAY", "", HttpStatus.BAD_GATEWAY);
		graphQLError = graphQLExceptionHandler.resolveToSingleError(restException, dataFetchingEnvironment);
		assertEquals("error BAD_GATEWAY", graphQLError.getMessage());
		assertEquals(ErrorType.INTERNAL_ERROR, graphQLError.getErrorType());

		AccessDeniedException accessDeniedException = new AccessDeniedException("error AccessDeniedException");
		graphQLError = graphQLExceptionHandler.resolveToSingleError(accessDeniedException, dataFetchingEnvironment);
		assertEquals("Access denied: you do not have the required permissions.", graphQLError.getMessage());
		assertEquals(ErrorType.FORBIDDEN, graphQLError.getErrorType());

		Exception exception = new Exception("error general");
		graphQLError = graphQLExceptionHandler.resolveToSingleError(exception, dataFetchingEnvironment);
		assertEquals(null, graphQLError);
	}
}
