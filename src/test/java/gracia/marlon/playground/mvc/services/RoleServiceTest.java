package gracia.marlon.playground.mvc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import gracia.marlon.playground.mvc.mapper.RoleMapper;
import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.repository.RoleRepository;

public class RoleServiceTest {

	private final RoleRepository roleRepository;

	private final RoleMapper roleMapper;

	private final RoleService RoleService;

	public RoleServiceTest() {
		this.roleRepository = Mockito.mock(RoleRepository.class);
		this.roleMapper = Mappers.getMapper(RoleMapper.class);
		this.RoleService = new RoleServiceImpl(roleRepository, roleMapper);
	}

	@Test
	public void getRolesNonEmptyRoles() {
		List<Role> testRole = new ArrayList<Role>();
		Role role1 = new Role(1L, "role1");
		Role role2 = new Role(2L, "role2");
		testRole.add(role1);
		testRole.add(role2);

		Mockito.when(roleRepository.findAll()).thenReturn(testRole);
		assertEquals(2, RoleService.getRoles().size());
	}

}
