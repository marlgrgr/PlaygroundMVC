package gracia.marlon.playground.mvc.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserService userService;

	private final UserRoleService userRoleService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		final UserDTO user = userService.getUserByUsername(username);

		final List<UserRoleDTO> userRoles = userRoleService.getAllUserRolesByUser(user.getId());

		final Set<SimpleGrantedAuthority> authorities = userRoles.stream().filter(x -> x.getRole() != null)
				.map(x -> new SimpleGrantedAuthority(x.getRole().getRole())).collect(Collectors.toSet());

		return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
				.password(user.getUsername()).authorities(authorities).build();
	}

}
