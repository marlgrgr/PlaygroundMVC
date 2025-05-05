package gracia.marlon.playground.mvc.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.model.UserRole;
import gracia.marlon.playground.mvc.model.Users;

public class UserRoleMapperTest {

	@Test
	public void toDtoSuccessful() {
		UserRoleDTO userRoleDTO = UserRoleMapper.INSTANCE.toDto(null);
		assertEquals(null, userRoleDTO);

		UserRole userRole = new UserRole();
		userRole.setId(1L);

		userRoleDTO = UserRoleMapper.INSTANCE.toDto(userRole);
		assertEquals(1L, userRoleDTO.getId().longValue());
		assertEquals(null, userRoleDTO.getRole());
		assertEquals(null, userRoleDTO.getUser());

		Role role = new Role();
		role.setId(1L);
		role.setRole("role");

		Users user = new Users();
		user.setId(1L);
		user.setFullname("fullname");
		user.setPassword("password");
		user.setPasswordChangeRequired(true);
		user.setRetired(false);
		user.setUsername("user");

		userRole = new UserRole();
		userRole.setId(1L);
		userRole.setRole(role);
		userRole.setUser(user);

		userRoleDTO = UserRoleMapper.INSTANCE.toDto(userRole);
		assertEquals(1L, userRoleDTO.getId().longValue());
		assertEquals("role", userRoleDTO.getRole().getRole());
		assertEquals("user", userRoleDTO.getUser().getUsername());
	}
}
