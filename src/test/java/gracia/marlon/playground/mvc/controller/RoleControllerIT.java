package gracia.marlon.playground.mvc.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.RoleDTO;

public class RoleControllerIT extends AbstractIntegrationBase {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void roleIT() throws Exception {
		MvcResult response = mockMvc.perform(get("/api/v1/roles").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		List<RoleDTO> roleList = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<List<RoleDTO>>() {
				});

		assertEquals(2, roleList.size());

		List<String> roleNameList = roleList.stream().map(role -> role.getRole()).collect(Collectors.toList());

		assertTrue(roleNameList.contains("ROLE_ADMIN"));
		assertTrue(roleNameList.contains("ROLE_USER"));
	}
}
