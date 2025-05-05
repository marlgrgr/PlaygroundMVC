package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.util.SharedConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTServiceTest {

	private final Environment env;

	private final JWTService jwtService;

	public JWTServiceTest() {
		this.env = Mockito.mock(Environment.class);
		Mockito.when(this.env.getProperty("jwt.secretKey", "")).thenReturn("5v8y/B?E(H+MbQeThWmZq4t7w!z%C&F)");
		Mockito.when(this.env.getProperty("jwt.expirationTime", "3600")).thenReturn("3600");

		this.jwtService = new JWTServiceImpl(this.env);
	}

	@Test
	public void generateAndValidateToken() {

		final Map<String, Object> claims = new HashMap<String, Object>();
		claims.put(SharedConstants.CLAIMS_PASSWORD_CHANGE_REQUIRED, false);
		claims.put(SharedConstants.CLAIMS_USER_ID, 1L);
		claims.put(SharedConstants.CLAIMS_USER_FULLNAME, "user");

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");

		String token = this.jwtService.generateToken(claims, user);

		Claims responseClaims = this.jwtService.extractAllClaims(token);
		String responseUsername = this.jwtService.extractUsername(token);
		Long responseId = this.jwtService.extractUserId(token);
		boolean responsePasswordChangeRequierd = this.jwtService.extractPasswordChangeRequired(token);

		assertEquals("user", responseUsername);
		assertEquals(1, responseId.longValue());
		assertEquals(false, responsePasswordChangeRequierd);
		assertEquals("user", responseClaims.get(SharedConstants.CLAIMS_USER_FULLNAME));

	}

	@Test
	public void isTokenValid() {
		final Map<String, Object> claims = new HashMap<String, Object>();
		claims.put(SharedConstants.CLAIMS_PASSWORD_CHANGE_REQUIRED, false);
		claims.put(SharedConstants.CLAIMS_USER_ID, 1L);
		claims.put(SharedConstants.CLAIMS_USER_FULLNAME, "user");

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");

		String validToken = this.jwtService.generateToken(claims, user);

		assertEquals(true, this.jwtService.isTokenValid(validToken, user));

		user.setUsername("diferentUser");
		assertEquals(false, this.jwtService.isTokenValid(validToken, user));

		String invalidToken = this.generateExpiredJWT(claims, user);
		assertEquals(false, this.jwtService.isTokenValid(invalidToken, user));

	}

	private String generateExpiredJWT(Map<String, Object> extraClaims, UserDTO user) {
		String secretKey = "5v8y/B?E(H+MbQeThWmZq4t7w!z%C&F)";

		return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis() - 5000))
				.setExpiration(new Date(System.currentTimeMillis() - 1000))
				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
				.compact();
	}
}
