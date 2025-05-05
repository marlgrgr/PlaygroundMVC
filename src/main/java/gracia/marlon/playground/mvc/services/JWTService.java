package gracia.marlon.playground.mvc.services;

import java.util.Map;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import io.jsonwebtoken.Claims;

public interface JWTService {

	String generateToken(Map<String, Object> extraClaims, UserDTO user);

	Claims extractAllClaims(String token);

	String extractUsername(String token);

	Long extractUserId(String token);

	boolean extractPasswordChangeRequired(String token);

	boolean isTokenValid(String token, UserDTO user);

}
