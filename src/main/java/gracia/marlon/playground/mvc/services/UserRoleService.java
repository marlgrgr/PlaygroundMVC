package gracia.marlon.playground.mvc.services;

import java.util.List;

import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;

public interface UserRoleService {

	PagedResponse<UserRoleDTO> getUserRoles(Integer page, Integer pageSize);

	PagedResponse<UserRoleDTO> getUserRolesByUser(Long userId, Integer page, Integer pageSize);

	PagedResponse<UserRoleDTO> getUserRolesByRole(Long roleId, Integer page, Integer pageSize);

	List<UserRoleDTO> getAllUserRolesByUser(Long userId);

	void createUserRole(UserRoleDTO userRoleDTO);

	void deleteUserRole(Long id);

}
