package gracia.marlon.playground.mvc.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.services.RoleService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RoleGraphQLController {

	private final RoleService roleService;

	@QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
	public List<RoleDTO> getRoleList() {
		final List<RoleDTO> roleList = this.roleService.getRoles();
		return roleList;
	}
}
