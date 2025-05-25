package gracia.marlon.playground.mvc.configuration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;

public class GraphQLExceptionHandlerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unchecked")
	@Test
	void handleForbiddenIT() throws Exception {

		String mutation = """
				mutation CreateUser {
				    createUser(user: { username: "newuser", password: "", fullname: "pass" })
				}
				""";

		String query = "{\"query\": \"" + mutation.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		MvcResult response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		Map<String, Object> responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		List<Map<String, Object>> errorList = objectMapper.convertValue(responseMap.get("errors"),
				new TypeReference<List<Map<String, Object>>>() {
				});

		Map<String, Object> extensionsMap = (Map<String, Object>) errorList.getFirst().get("extensions");

		assertEquals("400", extensionsMap.get("httpCode").toString());

		mutation = """
				mutation CreateUser {
				    createUser(user: { username: "newuser", password: "pass", fullname: "pass" })
				}
				""";

		query = "{\"query\": \"" + mutation.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> responseData = (Map<String, Object>) responseMap.get("data");
		Boolean responseBoolean = (Boolean) responseData.get("createUser");
		assertEquals(true, responseBoolean);

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUsername("newuser");
		authRequestDTO.setPassword("pass");

		response = mockMvc
				.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequestDTO)))
				.andExpect(status().isOk()).andReturn();

		String token = response.getResponse().getContentAsString();

		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setOldPassword("pass");
		changePasswordDTO.setNewPassword("Newuser*123");
		changePasswordDTO.setConfirmNewPassword("Newuser*123");

		mockMvc.perform(post("/api/v1/auth/changePassword").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordDTO)))
				.andExpect(status().isNoContent());

		authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setUsername("newuser");
		authRequestDTO.setPassword("Newuser*123");

		response = mockMvc
				.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(authRequestDTO)))
				.andExpect(status().isOk()).andReturn();

		token = response.getResponse().getContentAsString();

		String queryStr = """
				query GetUsers {
				    getUsers {
				        page
				        totalPages
				        totalResults
				        results {
				            id
				            passwordChangeRequired
				            fullname
				            username
				        }
				    }
				}
				""";

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		errorList = objectMapper.convertValue(responseMap.get("errors"),
				new TypeReference<List<Map<String, Object>>>() {
				});

		extensionsMap = (Map<String, Object>) errorList.getFirst().get("extensions");

		assertEquals("403", extensionsMap.get("httpCode").toString());

		queryStr = """
				query GetUsers {
				    getUsers {
				        page
				        totalPages
				        totalResults
				        results {
				            id
				            passwordChangeRequired
				            fullname
				            username
				        }
				    }
				}
				""";

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");
		PagedResponse<UserDTO> pagedResponse = objectMapper.convertValue(responseData.get("getUsers"),
				new TypeReference<PagedResponse<UserDTO>>() {
				});

		Long idFirstResult = pagedResponse.getResults().getFirst().getId();

		mutation = String.format("""
					mutation DeleteUser {
					    deleteUser(userId: \"%s\" )
					}
				""", idFirstResult);

		query = "{\"query\": \"" + mutation.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");
		responseBoolean = (Boolean) responseData.get("deleteUser");
		assertEquals(true, responseBoolean);

	}

}
