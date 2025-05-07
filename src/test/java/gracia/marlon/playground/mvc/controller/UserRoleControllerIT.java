package gracia.marlon.playground.mvc.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;

public class UserRoleControllerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void userRoleIT() throws Exception {
		MvcResult response = mockMvc
				.perform(get("/api/v1/userRoles").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		PagedResponse<UserRoleDTO> pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());

		UserRoleDTO userUserRole = pagedResponse.getResults().stream()
				.filter(userRole -> "ROLE_USER".equalsIgnoreCase(userRole.getRole().getRole())).findFirst().get();
		Long idRoleUser = userUserRole.getId();
		Long userRoleUser = userUserRole.getUser().getId();
		Long roleUser = userUserRole.getRole().getId();

		response = mockMvc.perform(
				get("/api/v1/userRoles/user/" + userRoleUser).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());
		
		response = mockMvc.perform(
				get("/api/v1/userRoles/user/" + userRoleUser + "/all").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		List<UserRoleDTO> userRoleList = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<List<UserRoleDTO>>() {
				});

		assertEquals(2L, userRoleList.size());

		response = mockMvc
				.perform(get("/api/v1/userRoles/role/" + roleUser).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(1L, pagedResponse.getTotalResults());

		mockMvc.perform(delete("/api/v1/userRoles/" + idRoleUser).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isNoContent());

		response = mockMvc.perform(get("/api/v1/userRoles").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(1L, pagedResponse.getTotalResults());

		UserDTO userDTO = new UserDTO();
		userDTO.setId(userRoleUser);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setId(roleUser);

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setUser(userDTO);
		userRoleDTO.setRole(roleDTO);

		mockMvc.perform(post("/api/v1/userRoles").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRoleDTO)))
				.andExpect(status().isCreated());

		response = mockMvc.perform(get("/api/v1/userRoles").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserRoleDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());
	}
}
