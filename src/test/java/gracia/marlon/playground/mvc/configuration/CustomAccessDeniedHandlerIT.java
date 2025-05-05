package gracia.marlon.playground.mvc.configuration;

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

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;

public class CustomAccessDeniedHandlerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void handleForbiddenIT() throws Exception {

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

}
