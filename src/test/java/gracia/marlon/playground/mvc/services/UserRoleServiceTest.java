package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.UserRoleMapper;
import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.model.UserRole;
import gracia.marlon.playground.mvc.model.Users;
import gracia.marlon.playground.mvc.repository.RoleRepository;
import gracia.marlon.playground.mvc.repository.UserRoleRepository;
import gracia.marlon.playground.mvc.repository.UsersRepository;

public class UserRoleServiceTest {

	private final UserRoleRepository userRoleRepository;

	private final UsersRepository usersRepository;

	private final RoleRepository roleRepository;

	private final CacheService cacheService;

	private final UserRoleMapper userRoleMapper;

	private final UserRoleService userRoleService;

	public UserRoleServiceTest() {
		this.userRoleRepository = Mockito.mock(UserRoleRepository.class);
		this.usersRepository = Mockito.mock(UsersRepository.class);
		this.roleRepository = Mockito.mock(RoleRepository.class);
		this.cacheService = Mockito.mock(CacheService.class);
		this.userRoleMapper = Mockito.mock(UserRoleMapper.class);
		this.userRoleService = new UserRoleServiceImpl(this.userRoleRepository, this.usersRepository,
				this.roleRepository, this.cacheService, this.userRoleMapper);
	}

	@Test
	public void getUserRolesSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		UserRole userRole = new UserRole();
		userRole.setId(1L);
		List<UserRole> content = new ArrayList<UserRole>();
		content.add(userRole);

		Page<UserRole> userRolePage = new PageImpl<UserRole>(content, pageable, content.size());

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);

		Mockito.when(this.userRoleRepository.findAllNotRetired(Mockito.any(Pageable.class))).thenReturn(userRolePage);
		Mockito.when(this.userRoleMapper.toDto(Mockito.any())).thenReturn(userRoleDTO);

		PagedResponse<UserRoleDTO> pagedResponse = this.userRoleService.getUserRoles(1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals(1L, pagedResponse.getResults().getFirst().getId().longValue());
	}

	@Test
	public void getUserRolesByUserSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		UserRole userRole = new UserRole();
		userRole.setId(1L);
		List<UserRole> content = new ArrayList<UserRole>();
		content.add(userRole);

		Page<UserRole> userRolePage = new PageImpl<UserRole>(content, pageable, content.size());

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);

		Mockito.when(this.userRoleRepository.findByUserId(Mockito.anyLong(), Mockito.any(Pageable.class)))
				.thenReturn(userRolePage);
		Mockito.when(this.userRoleMapper.toDto(Mockito.any())).thenReturn(userRoleDTO);

		PagedResponse<UserRoleDTO> pagedResponse = this.userRoleService.getUserRolesByUser(1L, 1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals(1L, pagedResponse.getResults().getFirst().getId().longValue());
	}

	@Test
	public void getUserRolesByRoleSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		UserRole userRole = new UserRole();
		userRole.setId(1L);
		List<UserRole> content = new ArrayList<UserRole>();
		content.add(userRole);

		Page<UserRole> userRolePage = new PageImpl<UserRole>(content, pageable, content.size());

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);

		Mockito.when(this.userRoleRepository.findByRoleId(Mockito.anyLong(), Mockito.any(Pageable.class)))
				.thenReturn(userRolePage);
		Mockito.when(this.userRoleMapper.toDto(Mockito.any())).thenReturn(userRoleDTO);

		PagedResponse<UserRoleDTO> pagedResponse = this.userRoleService.getUserRolesByRole(1L, 1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals(1L, pagedResponse.getResults().getFirst().getId().longValue());
	}

	@Test
	public void getAllUserRolesByUserSuccessful() {
		UserRole userRole = new UserRole();
		userRole.setId(1L);
		List<UserRole> content = new ArrayList<UserRole>();
		content.add(userRole);

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);

		Mockito.when(this.userRoleRepository.findAllByUserId(Mockito.anyLong())).thenReturn(content);
		Mockito.when(this.userRoleMapper.toDto(Mockito.any())).thenReturn(userRoleDTO);

		List<UserRoleDTO> userRoleDTOList = this.userRoleService.getAllUserRolesByUser(1L);

		assertEquals(1, userRoleDTOList.size());
		assertEquals(1L, userRoleDTOList.getFirst().getId().longValue());
	}

	@Test
	public void createUserRoleSuccessful() {

		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setId(1L);

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);
		userRoleDTO.setUser(userDTO);
		userRoleDTO.setRole(roleDTO);

		Users user = new Users();
		Role role = new Role();

		Optional<Users> optionalUser = Optional.of(user);
		Optional<Role> optinalRole = Optional.of(role);

		List<UserRole> content = new ArrayList<UserRole>();

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);
		Mockito.when(this.roleRepository.findById(Mockito.anyLong())).thenReturn(optinalRole);
		Mockito.when(this.userRoleRepository.findByUserIdAndRoleId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(content);
		Mockito.when(this.userRoleRepository.save(Mockito.any())).thenReturn(null);
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());

		this.userRoleService.createUserRole(userRoleDTO);

		Mockito.verify(this.cacheService, Mockito.times(8)).evictCache(Mockito.anyString());
	}

	@Test
	public void createUserRoleValidations() {
		RestException restException = assertThrows(RestException.class,
				() -> this.userRoleService.createUserRole(null));
		assertEquals("AUTH-0012", restException.getError().getCode());

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		restException = assertThrows(RestException.class, () -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0012", restException.getError().getCode());

		RoleDTO roleDTO = new RoleDTO();
		userRoleDTO.setRole(roleDTO);
		restException = assertThrows(RestException.class, () -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0012", restException.getError().getCode());

		roleDTO.setId(1L);
		userRoleDTO.setRole(roleDTO);
		restException = assertThrows(RestException.class, () -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0012", restException.getError().getCode());

		UserDTO userDTO = new UserDTO();
		userRoleDTO.setUser(userDTO);
		restException = assertThrows(RestException.class, () -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0012", restException.getError().getCode());
	}

	@Test
	public void createUserRoleEmptyUserRole() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setId(1L);

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);
		userRoleDTO.setUser(userDTO);
		userRoleDTO.setRole(roleDTO);

		Optional<Users> optionalUser = Optional.empty();

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);

		RestException restException = assertThrows(RestException.class,
				() -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0001", restException.getError().getCode());

		Users user = new Users();

		optionalUser = Optional.of(user);
		Optional<Role> optinalRole = Optional.empty();

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);
		Mockito.when(this.roleRepository.findById(Mockito.anyLong())).thenReturn(optinalRole);

		restException = assertThrows(RestException.class, () -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0011", restException.getError().getCode());
	}

	@Test
	public void createUserRoleExisting() {
		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);

		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setId(1L);

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		userRoleDTO.setId(1L);
		userRoleDTO.setUser(userDTO);
		userRoleDTO.setRole(roleDTO);

		Users user = new Users();
		Role role = new Role();

		Optional<Users> optionalUser = Optional.of(user);
		Optional<Role> optinalRole = Optional.of(role);

		UserRole userRole = new UserRole();

		List<UserRole> content = new ArrayList<UserRole>();
		content.add(userRole);

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);
		Mockito.when(this.roleRepository.findById(Mockito.anyLong())).thenReturn(optinalRole);
		Mockito.when(this.userRoleRepository.findByUserIdAndRoleId(Mockito.anyLong(), Mockito.anyLong()))
				.thenReturn(content);

		RestException restException = assertThrows(RestException.class,
				() -> this.userRoleService.createUserRole(userRoleDTO));
		assertEquals("AUTH-0011", restException.getError().getCode());
	}

	@Test
	public void deleteUserRoleSuccessful() {
		Mockito.doNothing().when(this.userRoleRepository).deleteById(Mockito.anyLong());
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());

		this.userRoleService.deleteUserRole(1L);

		Mockito.verify(this.cacheService, Mockito.times(8)).evictCache(Mockito.anyString());
	}
}
