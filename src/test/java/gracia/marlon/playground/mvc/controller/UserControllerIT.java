package gracia.marlon.playground.mvc.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;

public class UserControllerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void userIT() throws Exception {
		MvcResult response = mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		PagedResponse<UserDTO> pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserDTO>>() {
				});

		assertEquals(1L, pagedResponse.getTotalResults());

		Long idFirstResult = pagedResponse.getResults().getFirst().getId();

		response = mockMvc
				.perform(get("/api/v1/users/" + idFirstResult).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		UserDTO user = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
		assertEquals("admin", user.getUsername());

		response = mockMvc.perform(get("/api/v1/users/username/" + user.getUsername()).header("Authorization",
				"Bearer " + this.getToken())).andExpect(status().isOk()).andReturn();

		user = objectMapper.readValue(response.getResponse().getContentAsString(), UserDTO.class);
		assertEquals("admin", user.getUsername());

		UserWithPasswordDTO userWithPasswordDTO = new UserWithPasswordDTO();
		userWithPasswordDTO.setUsername("user");
		userWithPasswordDTO.setFullname("user");
		userWithPasswordDTO.setPassword("pass");

		mockMvc.perform(post("/api/v1/users").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userWithPasswordDTO)))
				.andExpect(status().isCreated());

		response = mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserDTO>>() {
				});

		assertEquals(2L, pagedResponse.getTotalResults());

		idFirstResult = pagedResponse.getResults().getFirst().getId();

		mockMvc.perform(delete("/api/v1/users/" + idFirstResult).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isNoContent());

		response = mockMvc.perform(get("/api/v1/users").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<UserDTO>>() {
				});

		assertEquals(1L, pagedResponse.getTotalResults());
		assertEquals(1L, pagedResponse.getResults().getFirst().getId().longValue());
	}

}
