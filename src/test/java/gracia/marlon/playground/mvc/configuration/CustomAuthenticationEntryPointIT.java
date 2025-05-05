package gracia.marlon.playground.mvc.configuration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public class CustomAuthenticationEntryPointIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void handleUnauthorizedIT() throws Exception {
		mockMvc.perform(get("/api/v1/users")).andExpect(status().isUnauthorized());
	}
}
