package gracia.marlon.playground.mvc.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;

public class AuthenticationControllerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void changePasswordNoToken() throws Exception {
		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setOldPassword("admin123");
		changePasswordDTO.setNewPassword("Admin*123");
		changePasswordDTO.setConfirmNewPassword("Admin*123");

		mockMvc.perform(post("/api/v1/auth/changePassword").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(changePasswordDTO))).andExpect(status().isBadRequest());

		mockMvc.perform(post("/api/v1/auth/changePassword").header("Authorization", "doesn't start with bearer")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(changePasswordDTO)))
				.andExpect(status().isBadRequest());
	}
}
