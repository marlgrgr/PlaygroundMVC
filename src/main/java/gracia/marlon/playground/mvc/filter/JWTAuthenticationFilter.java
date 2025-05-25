package gracia.marlon.playground.mvc.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.graphql.execution.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.services.JWTService;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JWTService jwtService;

	private final UserDetailsService userDetailsService;

	private final String PATH_TO_CHANGE_PASSWORD = "/api/v1/auth/changePassword";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		final String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		try {
			final String token = authHeader.substring(7);
			final String username = jwtService.extractUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				final UserDTO user = new UserDTO();
				user.setUsername(username);

				if (jwtService.isTokenValid(token, user)) {

					final boolean passwordChangeRequired = jwtService.extractPasswordChangeRequired(token);

					if (passwordChangeRequired && !request.getRequestURI().contains(this.PATH_TO_CHANGE_PASSWORD)) {
						if (request.getRequestURI().contains("/graphql")) {
							Map<String, Object> extensions = new HashMap<>();
							extensions.put("message", "You must change your password before using any other endpoint");
							extensions.put("code", "AUTH-0017");
							extensions.put("httpCode", 403);

							ErrorType errorType = ErrorType.FORBIDDEN;

							GraphQLError graphError = GraphqlErrorBuilder.newError()
									.message("You must change your password before using any other endpoint")
									.errorType(errorType).extensions(extensions).build();

							Map<String, Object> errorGraphDetails = graphError.toSpecification();
							Map<String, Object> responseMap = new HashMap<String, Object>();
							List<Object> errorMap = new ArrayList<Object>();
							errorMap.add(errorGraphDetails);
							responseMap.put("errors", errorMap);
							response.setStatus(HttpServletResponse.SC_OK);
							final ObjectMapper mapper = new ObjectMapper();
							response.getWriter().write(mapper.writeValueAsString(responseMap));
							return;
						} else {
							response.setStatus(HttpStatus.FORBIDDEN.value());
							response.setContentType("application/json");
							response.getWriter().write(
									"{\"message\":\"You must change your password before using any other endpoint\",\"code\":\"AUTH-0017\",\"httpCode\":403}");
							return;
						}
					}

					final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			log.error("An error occurred while verifying the security token", e);
		}

		chain.doFilter(request, response);
	}
}
