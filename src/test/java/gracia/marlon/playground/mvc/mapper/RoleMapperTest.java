package gracia.marlon.playground.mvc.mapper;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.model.Role;

public class RoleMapperTest {

	@Test
	public void toDtoSuccessful() {
		RoleDTO roleDTO = RoleMapper.INSTANCE.toDto(null);
		assertEquals(null, roleDTO);

		Role role = new Role();
		role.setId(1L);
		role.setRole("role");

		roleDTO = RoleMapper.INSTANCE.toDto(role);
		assertEquals(1L, roleDTO.getId().longValue());
		assertEquals("role", roleDTO.getRole());
	}
}
