package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;

public class CustomUserDetailsServiceTest {

	private final UserService userService;

	private final UserRoleService userRoleService;

	private final CustomUserDetailsService customUserDetailsService;

	public CustomUserDetailsServiceTest() {
		this.userService = Mockito.mock(UserService.class);
		this.userRoleService = Mockito.mock(UserRoleService.class);
		this.customUserDetailsService = new CustomUserDetailsService(this.userService, this.userRoleService);
	}

	@Test
	public void loadUserByUsernameSuccessful() {

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");
		
		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setRole("myrole");
		
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRole(roleDTO);
		
		List<UserRoleDTO> userRoles = new ArrayList<UserRoleDTO>();
		userRoles.add(userRoleDTO);

		Mockito.when(this.userService.getUserByUsername(Mockito.anyString())).thenReturn(user);
		Mockito.when(this.userRoleService.getAllUserRolesByUser(Mockito.anyLong())).thenReturn(userRoles);

		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername("user");
		
		assertEquals("user", userDetails.getUsername());
		assertEquals(1, userDetails.getAuthorities().size());
	}
	
	@Test
	public void loadUserByUsernameRoleNull() {

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");
		
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRole(null);
		
		List<UserRoleDTO> userRoles = new ArrayList<UserRoleDTO>();
		userRoles.add(userRoleDTO);

		Mockito.when(this.userService.getUserByUsername(Mockito.anyString())).thenReturn(user);
		Mockito.when(this.userRoleService.getAllUserRolesByUser(Mockito.anyLong())).thenReturn(userRoles);

		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername("user");
		
		assertEquals("user", userDetails.getUsername());
		assertEquals(0, userDetails.getAuthorities().size());
	}
	
	@Test
	public void loadUserByUsernameUserEmptyRole() {

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");
		
		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setRole(null);
		
		List<UserRoleDTO> userRoles = new ArrayList<UserRoleDTO>();

		Mockito.when(this.userService.getUserByUsername(Mockito.anyString())).thenReturn(user);
		Mockito.when(this.userRoleService.getAllUserRolesByUser(Mockito.anyLong())).thenReturn(userRoles);

		UserDetails userDetails = this.customUserDetailsService.loadUserByUsername("user");
		
		assertEquals("user", userDetails.getUsername());
		assertEquals(0, userDetails.getAuthorities().size());
	}

}
