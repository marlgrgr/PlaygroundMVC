package gracia.marlon.playground.mvc.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		response.setContentType("application/json");

		if (request.getRequestURI().contains("/graphql")) {
			Map<String, Object> extensions = new HashMap<>();
			extensions.put("message", "Unauthorized: authentication is required.");
			extensions.put("code", "AUTH-0020");
			extensions.put("httpCode", 401);

			ErrorType errorType = ErrorType.UNAUTHORIZED;

			GraphQLError graphError = GraphqlErrorBuilder.newError()
					.message("Unauthorized: authentication is required.").errorType(errorType).extensions(extensions)
					.build();

			Map<String, Object> errorGraphDetails = graphError.toSpecification();
			Map<String, Object> responseMap = new HashMap<String, Object>();
			List<Object> errorMap = new ArrayList<Object>();
			errorMap.add(errorGraphDetails);
			responseMap.put("errors", errorMap);
			response.setStatus(HttpServletResponse.SC_OK);
			final ObjectMapper mapper = new ObjectMapper();
			response.getWriter().write(mapper.writeValueAsString(responseMap));
			return;
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		final Map<String, Object> errorDetails = new HashMap<>();
		errorDetails.put("message", "Unauthorized: authentication is required.");
		errorDetails.put("code", "AUTH-0020");
		errorDetails.put("httpCode", 401);

		final ObjectMapper mapper = new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(errorDetails));
	}
}
