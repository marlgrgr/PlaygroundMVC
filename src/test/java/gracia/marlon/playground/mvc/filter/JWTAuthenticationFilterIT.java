package gracia.marlon.playground.mvc.filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;
import gracia.marlon.playground.mvc.util.SharedConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTAuthenticationFilterIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Test
	void JWTWithoutSubject() throws Exception {
		final Map<String, Object> claims = new HashMap<String, Object>();
		claims.put(SharedConstants.CLAIMS_PASSWORD_CHANGE_REQUIRED, false);
		claims.put(SharedConstants.CLAIMS_USER_ID, 1L);
		claims.put(SharedConstants.CLAIMS_USER_FULLNAME, "user");

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");

		String token = generateJWTWithoutSubject(claims, user);

		mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void JWTChangePasswordRequired() throws Exception {

		UserWithPasswordDTO userWithPasswordDTO = new UserWithPasswordDTO();
		userWithPasswordDTO.setUsername("newuser");
		userWithPasswordDTO.setFullname("user");
		userWithPasswordDTO.setPassword("pass");

		mockMvc.perform(post("/api/v1/users").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userWithPasswordDTO)))
				.andExpect(status().isCreated());

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUsername("newuser");
		authRequestDTO.setPassword("pass");

		MvcResult response = mockMvc
				.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequestDTO)))
				.andExpect(status().isOk()).andReturn();

		String token = response.getResponse().getContentAsString();

		mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden());

		response = mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		PagedResponse<UserDTO> pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserDTO>>() {
				});

		Long idFirstResult = pagedResponse.getResults().getFirst().getId();

		mockMvc.perform(delete("/api/v1/users/" + idFirstResult).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isNoContent());

	}

	@Test
	void JWTTokenException() throws Exception {
		mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer nonvalidtoken"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void JWTAlreadyLogged() throws Exception {
		UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

		final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authToken);

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUsername("admin");
		authRequestDTO.setPassword("Admin*123");

		MvcResult response = mockMvc
				.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequestDTO)))
				.andExpect(status().isOk()).andReturn();

		String token = response.getResponse().getContentAsString();

		mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
	}

	@Test
	void JWTExpiredToken() throws Exception {
		final Map<String, Object> claims = new HashMap<String, Object>();
		claims.put(SharedConstants.CLAIMS_PASSWORD_CHANGE_REQUIRED, false);
		claims.put(SharedConstants.CLAIMS_USER_ID, 1L);
		claims.put(SharedConstants.CLAIMS_USER_FULLNAME, "admin");

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("admin");

		String token = generateExpiredJWT(claims, user);

		mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + token))
				.andExpect(status().isUnauthorized());
	}

	private String generateJWTWithoutSubject(Map<String, Object> extraClaims, UserDTO user) {
		String secretKey = "5v8y/B?E(H+MbQeThWmZq4t7w!z%C&F)";

		return Jwts.builder().setClaims(extraClaims).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 3600000))
				.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
				.compact();
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
