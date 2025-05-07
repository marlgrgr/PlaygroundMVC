package gracia.marlon.playground.mvc.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.UserMapper;
import gracia.marlon.playground.mvc.model.Users;
import gracia.marlon.playground.mvc.repository.UsersRepository;
import gracia.marlon.playground.mvc.util.PageableUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UsersRepository usersRepository;

	private final PasswordEncoder passwordEncoder;

	private final CacheService cacheService;

	private final UserMapper userMapper;

	private final int MIN_USER_LENGTH = 2;

	private final int MAX_USER_LENGTH = 20;

	@Override
	@Cacheable(cacheNames = "UserService_getUsers", key = "#page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<UserDTO> getUsers(Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("id"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<Users> userPage = this.usersRepository.findNotRetiredUsers(pageable);

		final List<UserDTO> users = userPage.get().map(this.userMapper::toDto).collect(Collectors.toList());

		final PagedResponse<UserDTO> pagedResponse = PageableUtil.getPagedResponse(userPage, users);

		return pagedResponse;
	}

	@Override
	@Cacheable(cacheNames = "UserService_getUserById", key = "#userId", unless = "#result == null")
	public UserDTO getUserById(Long userId) {
		if (userId == null) {
			throw new RestException("User not found", "AUTH-0001", HttpStatus.NOT_FOUND);
		}

		final Optional<Users> userOpt = this.usersRepository.findById(userId);

		if (userOpt.isEmpty()) {
			throw new RestException("User not found", "AUTH-0001", HttpStatus.NOT_FOUND);
		}

		final UserDTO user = this.userMapper.toDto(userOpt.get());
		return user;
	}

	@Override
	@Cacheable(cacheNames = "UserService_getUserByUsername", key = "#username", unless = "#result == null")
	public UserDTO getUserByUsername(String username) {
		if (username == null) {
			throw new RestException("User not found", "AUTH-0001", HttpStatus.NOT_FOUND);
		}

		final List<Users> userOpt = this.usersRepository.findByUsername(username.trim().toLowerCase());

		if (userOpt.isEmpty()) {
			throw new RestException("User not found", "AUTH-0001", HttpStatus.NOT_FOUND);
		}

		Users userFirst = userOpt.getFirst();
		final UserDTO user = this.userMapper.toDto(userFirst);
		return user;
	}

	@Override
	@Transactional
	public void createUser(UserWithPasswordDTO userDto) {
		this.validateUser(userDto);
		final Users user = new Users();
		user.setUsername(userDto.getUsername().trim().toLowerCase());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setFullname(userDto.getFullname());
		user.setPasswordChangeRequired(true);

		this.usersRepository.save(user);
		this.evictUserCaches();
	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		this.usersRepository.retireById(userId);
		this.evictUserCaches();
	}

	@Override
	@Transactional
	public void changePassword(Long userId, ChangePasswordDTO changePasswordDTO) {
		final Users user = this.usersRepository.findById(userId).get();
		user.setPassword(this.passwordEncoder.encode(changePasswordDTO.getNewPassword()));
		user.setPasswordChangeRequired(false);
		this.usersRepository.save(user);
		this.evictUserCaches();
	}

	@Override
	public boolean validUser(AuthRequestDTO request) {
		if (request == null || request.getUsername() == null || request.getUsername().trim().isEmpty()
				|| request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			return false;
		}

		final List<Users> userList = this.usersRepository.findByUsername(request.getUsername());

		if (!userList.isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			if (passwordEncoder.matches(request.getPassword(), userList.getFirst().getPassword())) {
				return true;
			}
		}
		return false;
	}

	private void validateUser(UserWithPasswordDTO userDto) {
		if (userDto == null) {
			throw new RestException("No user sent", "AUTH-0002", HttpStatus.BAD_REQUEST);
		}

		final int userLength = userDto.getUsername() == null ? 0 : userDto.getUsername().trim().length();

		if (userLength < this.MIN_USER_LENGTH || userLength > this.MAX_USER_LENGTH) {
			throw new RestException("Username doesn't have required length", "AUTH-0003", HttpStatus.BAD_REQUEST);
		}

		final List<Users> userOpt = this.usersRepository.findByUsername(userDto.getUsername().trim().toLowerCase());

		if (!userOpt.isEmpty()) {
			throw new RestException("Username already exist", "AUTH-0004", HttpStatus.BAD_REQUEST);
		}

		if (userDto.getPassword() == null || userDto.getPassword().trim().length() == 0) {
			throw new RestException("Invalid password", "AUTH-0005", HttpStatus.BAD_REQUEST);
		}

		if (userDto.getFullname() == null || userDto.getFullname().trim().length() == 0) {
			throw new RestException("Fullname can not be empty", "AUTH-0006", HttpStatus.BAD_REQUEST);
		}
	}

	private void evictUserCaches() {
		this.cacheService.evictCache("UserService_getUsers");
		this.cacheService.evictCache("UserService_getUserById");
		this.cacheService.evictCache("UserService_getUserByUsername");
	}
}
