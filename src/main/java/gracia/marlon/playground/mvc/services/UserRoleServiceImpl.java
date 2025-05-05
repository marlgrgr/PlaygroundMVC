package gracia.marlon.playground.mvc.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.UserRoleMapper;
import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.model.UserRole;
import gracia.marlon.playground.mvc.model.Users;
import gracia.marlon.playground.mvc.repository.RoleRepository;
import gracia.marlon.playground.mvc.repository.UserRoleRepository;
import gracia.marlon.playground.mvc.repository.UsersRepository;
import gracia.marlon.playground.mvc.util.PageableUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

	private final UserRoleRepository userRoleRepository;

	private final UsersRepository usersRepository;

	private final RoleRepository roleRepository;

	private final CacheService cacheService;

	private final UserRoleMapper userRoleMapper;

	@Override
	@Cacheable(cacheNames = "UserRoleService_getUserRoles", key = "#page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<UserRoleDTO> getUserRoles(Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("id"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<UserRole> userRolePage = this.userRoleRepository.findAllNotRetired(pageable);

		final List<UserRoleDTO> userRoleDTOList = userRolePage.get().map(this.userRoleMapper::toDto)
				.collect(Collectors.toList());

		final PagedResponse<UserRoleDTO> pagedResponse = PageableUtil.getPagedResponse(userRolePage, userRoleDTOList);

		return pagedResponse;
	}

	@Override
	@Cacheable(cacheNames = "UserRoleService_getUserRolesByUser", key = "#userId + '-' + #page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<UserRoleDTO> getUserRolesByUser(Long userId, Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("id"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<UserRole> userRolePage = this.userRoleRepository.findByUserId(userId, pageable);

		final List<UserRoleDTO> userRoleDTOList = userRolePage.get().map(this.userRoleMapper::toDto)
				.collect(Collectors.toList());

		final PagedResponse<UserRoleDTO> pagedResponse = PageableUtil.getPagedResponse(userRolePage, userRoleDTOList);

		return pagedResponse;
	}

	@Override
	@Cacheable(cacheNames = "UserRoleService_getUserRolesByRole", key = "#roleId + '-' + #page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<UserRoleDTO> getUserRolesByRole(Long roleId, Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("id"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<UserRole> userRolePage = this.userRoleRepository.findByRoleId(roleId, pageable);

		final List<UserRoleDTO> userRoleDTOList = userRolePage.get().map(this.userRoleMapper::toDto)
				.collect(Collectors.toList());

		final PagedResponse<UserRoleDTO> pagedResponse = PageableUtil.getPagedResponse(userRolePage, userRoleDTOList);

		return pagedResponse;
	}

	@Override
	@Cacheable(cacheNames = "UserRoleService_getAllUserRolesByUser", key = "#userId", unless = "#result == null")
	public List<UserRoleDTO> getAllUserRolesByUser(Long userId) {
		final List<UserRole> userRoleList = this.userRoleRepository.findAllByUserId(userId);

		final List<UserRoleDTO> userRoleDTOList = userRoleList.stream().map(this.userRoleMapper::toDto)
				.collect(Collectors.toList());

		return userRoleDTOList;
	}

	@Override
	@Transactional
	public void createUserRole(UserRoleDTO userRoleDTO) {

		this.validateUserRoleDTO(userRoleDTO);

		final Optional<Users> user = this.usersRepository.findById(userRoleDTO.getUser().getId());
		final Optional<Role> role = this.roleRepository.findById(userRoleDTO.getRole().getId());

		this.validateUserRoleExistence(user, role);

		final List<UserRole> userRoleList = this.userRoleRepository.findByUserIdAndRoleId(userRoleDTO.getUser().getId(),
				userRoleDTO.getRole().getId());
		if (!userRoleList.isEmpty()) {
			throw new RestException("User-Role already exist", "AUTH-0011", HttpStatus.BAD_REQUEST);
		}

		final UserRole userRole = new UserRole(null, user.get(), role.get());
		this.userRoleRepository.save(userRole);
		this.evictUserRoleCaches();

	}

	@Override
	@Transactional
	public void deleteUserRole(Long id) {
		this.userRoleRepository.deleteById(id);
		this.evictUserRoleCaches();
	}

	private void validateUserRoleDTO(UserRoleDTO userRoleDTO) {
		if (userRoleDTO == null || userRoleDTO.getRole() == null || userRoleDTO.getRole().getId() == null
				|| userRoleDTO.getUser() == null || userRoleDTO.getUser().getId() == null) {
			throw new RestException("The user-object sent is not valid", "AUTH-0012", HttpStatus.BAD_REQUEST);
		}
	}

	private void validateUserRoleExistence(Optional<Users> user, Optional<Role> role) {

		if (user.isEmpty()) {
			throw new RestException("User not found", "AUTH-0001", HttpStatus.NOT_FOUND);
		}

		if (role.isEmpty()) {
			throw new RestException("Role not found", "AUTH-0011", HttpStatus.NOT_FOUND);
		}
	}

	private void evictUserRoleCaches() {
		this.cacheService.evictCache("UserRoleService_getUserRoles");
		this.cacheService.evictCache("UserRoleService_getUserRolesByUser");
		this.cacheService.evictCache("UserRoleService_getUserRolesByRole");
		this.cacheService.evictCache("UserRoleService_getAllUserRolesByUser");
	}

}
