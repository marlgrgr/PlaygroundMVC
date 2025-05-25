package gracia.marlon.playground.mvc.graphql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.RoleDTO;

public class RoleGraphQLControllerIT extends AbstractIntegrationBase {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SuppressWarnings("unchecked")
	@Test
	void roleIT() throws Exception {

		String queryStr = """
				query GetRoleList {
				    getRoleList {
				        id
				        role
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
		List<RoleDTO> roleList = objectMapper.convertValue(responseData.get("getRoleList"),
				new TypeReference<List<RoleDTO>>() {
				});

		assertEquals(2, roleList.size());

		List<String> roleNameList = roleList.stream().map(role -> role.getRole()).collect(Collectors.toList());

		assertTrue(roleNameList.contains("ROLE_ADMIN"));
		assertTrue(roleNameList.contains("ROLE_USER"));
	}
}
