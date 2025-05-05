package gracia.marlon.playground.mvc.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.model.Users;

public class UserMapperTest {

	@Test
	public void toDtoSuccessful() {
		UserDTO userDTO = UserMapper.INSTANCE.toDto(null);
		assertEquals(null, userDTO);

		Users user = new Users();
		user.setId(1L);
		user.setFullname("fullname");
		user.setPassword("password");
		user.setPasswordChangeRequired(true);
		user.setRetired(false);
		user.setUsername("user");

		userDTO = UserMapper.INSTANCE.toDto(user);
		assertEquals(1L, userDTO.getId().longValue());
		assertEquals("user", userDTO.getUsername());
	}
}
