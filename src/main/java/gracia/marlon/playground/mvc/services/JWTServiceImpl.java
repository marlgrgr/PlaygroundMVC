package gracia.marlon.playground.mvc.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.util.SharedConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService {

	private final String secretKey;

	private final Long expirationTime;

	private final long SECONDS = 1000;

	private Key key;

	public JWTServiceImpl(Environment env) {
		this.secretKey = env.getProperty("jwt.secretKey", "");
		this.expirationTime = Long.parseLong(env.getProperty("jwt.expirationTime", "3600"));
	}

	@Override
	public String generateToken(Map<String, Object> extraClaims, UserDTO user) {
		return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + this.SECONDS * this.expirationTime))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	@Override
	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).setAllowedClockSkewSeconds(Integer.MAX_VALUE).build()
				.parseClaimsJws(token).getBody();
	}

	@Override
	public String extractUsername(String token) {
		return this.extractAllClaims(token).getSubject();
	}

	@Override
	public Long extractUserId(String token) {
		return ((Number) extractAllClaims(token).get(SharedConstants.CLAIMS_USER_ID)).longValue();
	}

	@Override
	public boolean extractPasswordChangeRequired(String token) {
		return (Boolean) extractAllClaims(token).get(SharedConstants.CLAIMS_PASSWORD_CHANGE_REQUIRED);
	}

	@Override
	public boolean isTokenValid(String token, UserDTO user) {
		if (this.isTokenExpired(token)) {
			return false;
		}
		final String username = this.extractUsername(token);
		return (username.equals(user.getUsername()));
	}

	private Key getSigningKey() {
		if (this.key == null) {
			this.key = Keys.hmacShaKeyFor(this.secretKey.getBytes(StandardCharsets.UTF_8));
		}

		return this.key;
	}

	private boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

}
