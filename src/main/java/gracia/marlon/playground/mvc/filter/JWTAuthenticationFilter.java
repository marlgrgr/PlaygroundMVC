package gracia.marlon.playground.mvc.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.services.JWTService;
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
						response.setStatus(HttpStatus.FORBIDDEN.value());
						response.setContentType("application/json");
						response.getWriter().write(
								"{\"message\":\"You must change your password before using any other endpoint\",\"code\":\"AUTH-0017\",\"httpCode\":403}");
						return;
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
