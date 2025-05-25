package gracia.marlon.playground.mvc.graphql;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.services.UserRoleService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserRoleGraphQLController {

	private final UserRoleService userRoleService;

	@QueryMapping
	@PreAuthorize("hasRole('ADMIN')")
	public PagedResponse<UserRoleDTO> getUserRoleList(@Argument Integer page, @Argument Integer pageSize) {
		final PagedResponse<UserRoleDTO> pagedResponse = this.userRoleService.getUserRoles(page, pageSize);
		return pagedResponse;
	}

	@QueryMapping
	@PreAuthorize("hasRole('ADMIN')")
	public PagedResponse<UserRoleDTO> getUserRoleListByUserId(@Argument Long userId, @Argument Integer page,
			@Argument Integer pageSize) {
		final PagedResponse<UserRoleDTO> pagedResponse = this.userRoleService.getUserRolesByUser(userId, page,
				pageSize);
		return pagedResponse;
	}

	@QueryMapping
	@PreAuthorize("hasRole('ADMIN')")
	public PagedResponse<UserRoleDTO> getUserRoleListByRoleId(@Argument Long roleId, @Argument Integer page,
			@Argument Integer pageSize) {
		final PagedResponse<UserRoleDTO> pagedResponse = this.userRoleService.getUserRolesByRole(roleId, page,
				pageSize);
		return pagedResponse;
	}

	@QueryMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserRoleDTO> getAllUserRoleListByUserId(@Argument Long userId) {
		final List<UserRoleDTO> userRoleList = this.userRoleService.getAllUserRolesByUser(userId);
		return userRoleList;
	}

	@MutationMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Boolean createUserRole(@Argument("userRole") UserRoleDTO userRoleDTO) {
		this.userRoleService.createUserRole(userRoleDTO);
		return true;
	}

	@MutationMapping
	@PreAuthorize("hasRole('ADMIN')")
	public Boolean deleteUserRole(@Argument Long userRoleId) {
		this.userRoleService.deleteUserRole(userRoleId);
		return true;
	}
}
