package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.UserMapper;
import gracia.marlon.playground.mvc.model.Users;
import gracia.marlon.playground.mvc.repository.UsersRepository;

public class UserServiceTest {

	private final UsersRepository usersRepository;

	private final PasswordEncoder passwordEncoder;

	private final CacheService cacheService;

	private final UserMapper userMapper;

	private final UserService userService;

	public UserServiceTest() {
		this.usersRepository = Mockito.mock(UsersRepository.class);
		this.passwordEncoder = Mockito.mock(PasswordEncoder.class);
		this.cacheService = Mockito.mock(CacheService.class);
		this.userMapper = Mockito.mock(UserMapper.class);
		this.userService = new UserServiceImpl(this.usersRepository, this.passwordEncoder, this.cacheService,
				this.userMapper);
	}

	@Test
	public void getUsersSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		Users user = new Users();
		user.setId(1L);
		List<Users> content = new ArrayList<Users>();
		content.add(user);

		Page<Users> userPage = new PageImpl<Users>(content, pageable, content.size());

		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);

		Mockito.when(this.usersRepository.findNotRetiredUsers(Mockito.any(Pageable.class))).thenReturn(userPage);
		Mockito.when(this.userMapper.toDto(Mockito.any())).thenReturn(userDTO);

		PagedResponse<UserDTO> pagedResponse = this.userService.getUsers(1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals(1L, pagedResponse.getResults().getFirst().getId().longValue());
	}

	@Test
	public void getUserByIdSuccessful() {
		Users user = new Users();
		user.setId(1L);
		Optional<Users> optionalUser = Optional.of(user);

		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);
		Mockito.when(this.userMapper.toDto(Mockito.any())).thenReturn(userDTO);

		UserDTO response = this.userService.getUserById(1L);

		assertEquals(1L, response.getId().longValue());
	}

	@Test
	public void getUserByIdNotExisting() {
		RestException restException = assertThrows(RestException.class, () -> this.userService.getUserById(null));
		assertEquals("AUTH-0001", restException.getError().getCode());

		Optional<Users> optionalUser = Optional.empty();

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);

		restException = assertThrows(RestException.class, () -> this.userService.getUserById(1L));
		assertEquals("AUTH-0001", restException.getError().getCode());
	}

	@Test
	public void getUserByUsernameSuccessful() {
		Users user = new Users();
		user.setId(1L);
		List<Users> userList = new ArrayList<Users>();
		userList.add(user);

		UserDTO userDTO = new UserDTO();
		userDTO.setId(1L);

		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		Mockito.when(this.userMapper.toDto(Mockito.any())).thenReturn(userDTO);

		UserDTO response = this.userService.getUserByUsername("user");

		assertEquals(1L, response.getId().longValue());
	}

	@Test
	public void getUserByUsernameNotExisting() {
		RestException restException = assertThrows(RestException.class, () -> this.userService.getUserByUsername(null));
		assertEquals("AUTH-0001", restException.getError().getCode());

		List<Users> userList = new ArrayList<Users>();

		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);

		restException = assertThrows(RestException.class, () -> this.userService.getUserByUsername("user"));
		assertEquals("AUTH-0001", restException.getError().getCode());
	}

	@Test
	public void createUserSuccessful() {
		UserWithPasswordDTO userWithPasswordDTO = new UserWithPasswordDTO();
		userWithPasswordDTO.setUsername("user");
		userWithPasswordDTO.setPassword("pass");
		userWithPasswordDTO.setFullname("user");

		List<Users> userList = new ArrayList<Users>();

		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		Mockito.when(this.passwordEncoder.encode(Mockito.anyString())).thenReturn("passEncode");
		Mockito.when(this.usersRepository.save(Mockito.any())).thenReturn(null);
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());

		this.userService.createUser(userWithPasswordDTO);

		Mockito.verify(this.cacheService, times(3)).evictCache(Mockito.anyString());

	}

	@Test
	public void createUserValidations() {
		RestException restException = assertThrows(RestException.class, () -> this.userService.createUser(null));
		assertEquals("AUTH-0002", restException.getError().getCode());

		UserWithPasswordDTO userWithPasswordDTO = new UserWithPasswordDTO();
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0003", restException.getError().getCode());

		userWithPasswordDTO.setUsername("averylongusernamethatdoesntpassthemaxamountofcharvalidations");
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0003", restException.getError().getCode());

		userWithPasswordDTO.setUsername("user");
		Users user = new Users();
		user.setId(1L);
		List<Users> userList = new ArrayList<Users>();
		userList.add(user);
		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0004", restException.getError().getCode());

		userList = new ArrayList<Users>();
		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0005", restException.getError().getCode());

		userWithPasswordDTO.setPassword("");
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0005", restException.getError().getCode());

		userWithPasswordDTO.setPassword("pass");
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0006", restException.getError().getCode());

		userWithPasswordDTO.setFullname("");
		restException = assertThrows(RestException.class, () -> this.userService.createUser(userWithPasswordDTO));
		assertEquals("AUTH-0006", restException.getError().getCode());
	}

	@Test
	public void deleteUserSuccessful() {
		Mockito.when(this.usersRepository.retireById(Mockito.anyLong())).thenReturn(0);
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());

		this.userService.deleteUser(1L);

		Mockito.verify(this.cacheService, times(3)).evictCache(Mockito.anyString());
	}

	@Test
	public void changePasswordSuccessful() {
		Users user = new Users();
		user.setId(1L);
		Optional<Users> optionalUser = Optional.of(user);

		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setNewPassword("newPass");

		Mockito.when(this.usersRepository.findById(Mockito.anyLong())).thenReturn(optionalUser);
		Mockito.when(this.passwordEncoder.encode(Mockito.anyString())).thenReturn("passEncode");
		Mockito.when(this.usersRepository.save(Mockito.any())).thenReturn(null);
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());

		this.userService.changePassword(1L, changePasswordDTO);

		Mockito.verify(this.cacheService, times(3)).evictCache(Mockito.anyString());
	}

	@Test
	public void validUserSuccessful() {
		AuthRequestDTO request = new AuthRequestDTO();
		request.setUsername("user");
		request.setPassword("pass");

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String passEncode = passwordEncoder.encode("pass");

		Users user = new Users();
		user.setId(1L);
		user.setPassword(passEncode);
		List<Users> userList = new ArrayList<Users>();
		userList.add(user);

		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);

		assertEquals(true, this.userService.validUser(request));
	}
	
	@Test
	public void validUserValidations() {
		assertEquals(false, this.userService.validUser(null));
		
		AuthRequestDTO request = new AuthRequestDTO();
		assertEquals(false, this.userService.validUser(request));
		
		request.setUsername("");
		assertEquals(false, this.userService.validUser(request));
		
		request.setUsername("user");
		assertEquals(false, this.userService.validUser(request));
		
		request.setPassword("");
		assertEquals(false, this.userService.validUser(request));
		
		request.setPassword("pass");
		List<Users> userList = new ArrayList<Users>();
		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		assertEquals(false, this.userService.validUser(request));
		
		Users user = new Users();
		user.setId(1L);
		user.setPassword("passwordEncodedThatDoesntMatch");
		userList = new ArrayList<Users>();
		userList.add(user);
		Mockito.when(this.usersRepository.findByUsername(Mockito.anyString())).thenReturn(userList);
		assertEquals(false, this.userService.validUser(request));
	}
}
