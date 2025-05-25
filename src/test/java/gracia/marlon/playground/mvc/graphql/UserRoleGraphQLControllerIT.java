package gracia.marlon.playground.mvc.graphql;

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

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;

public class UserRoleGraphQLControllerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unchecked")
	@Test
	void userRoleIT() throws Exception {

		String queryStr = """
				query GetUserRoleList {
				    getUserRoleList {
				        page
				        totalPages
				        totalResults
				        results {
				            id
				            role {
				                id
				                role
				            }
				            user {
				                id
				            }
				        }
				    }
				}
				""";

		String query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		MvcResult response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		Map<String, Object> responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> responseData = (Map<String, Object>) responseMap.get("data");
		PagedResponse<UserRoleDTO> pagedResponse = objectMapper.convertValue(responseData.get("getUserRoleList"),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());

		UserRoleDTO userUserRole = pagedResponse.getResults().stream()
				.filter(userRole -> "ROLE_USER".equalsIgnoreCase(userRole.getRole().getRole())).findFirst().get();
		Long idRoleUser = userUserRole.getId();
		Long userRoleUser = userUserRole.getUser().getId();
		Long roleUser = userUserRole.getRole().getId();

		queryStr = String.format("""
				query GetUserRoleListByUserId {
				    getUserRoleListByUserId(userId: \"%s\" ) {
				        page
				        totalPages
				        totalResults
				    }
				}
				""", userRoleUser);

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");

		pagedResponse = objectMapper.convertValue(responseData.get("getUserRoleListByUserId"),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());

		queryStr = String.format("""
				query GetAllUserRoleListByUserId {
				    getAllUserRoleListByUserId(userId: \"%s\" ) {
				        id
				    }
				}
				""", userRoleUser);

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");

		List<UserRoleDTO> userRoleList = objectMapper.convertValue(responseData.get("getAllUserRoleListByUserId"),
				new TypeReference<List<UserRoleDTO>>() {
				});

		assertEquals(2L, userRoleList.size());

		queryStr = String.format("""
					query GetUserRoleListByRoleId {
					    getUserRoleListByRoleId(roleId: \"%s\" ) {
					        page
					        totalPages
					        totalResults
					    }
					}
				""", roleUser);

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");

		pagedResponse = objectMapper.convertValue(responseData.get("getUserRoleListByRoleId"),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(1L, pagedResponse.getTotalResults());

		String mutation = String.format("""
					mutation DeleteUserRole {
					    deleteUserRole(userRoleId: \"%s\" )
					}
				""", idRoleUser);

		query = "{\"query\": \"" + mutation.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");
		Boolean responseBoolean = (Boolean) responseData.get("deleteUserRole");
		assertEquals(true, responseBoolean);

		queryStr = """
				query GetUserRoleList {
				    getUserRoleList {
				        page
				        totalPages
				        totalResults
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
		pagedResponse = objectMapper.convertValue(responseData.get("getUserRoleList"),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(1L, pagedResponse.getTotalResults());

		mutation = String.format("""
				mutation CreateUserRole {
				    createUserRole(userRole: { user: { id: "%s" }, role: { id: "%s" } })
				}
				""", userRoleUser, roleUser);

		query = "{\"query\": \"" + mutation.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");
		responseBoolean = (Boolean) responseData.get("createUserRole");
		assertEquals(true, responseBoolean);

		queryStr = """
				query GetUserRoleList {
				    getUserRoleList {
				        page
				        totalPages
				        totalResults
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
		pagedResponse = objectMapper.convertValue(responseData.get("getUserRoleList"),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());
	}
}
