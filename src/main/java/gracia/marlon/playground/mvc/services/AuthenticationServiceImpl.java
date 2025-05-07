package gracia.marlon.playground.mvc.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.util.SharedConstants;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final JWTService jwtService;

	private final UserService userService;

	private final UserRoleService userRoleService;

	private final String PASSWORD_REGEX_VALIDATION = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";

	private final Pattern pattern = Pattern.compile(PASSWORD_REGEX_VALIDATION);

	@Override
	public String login(AuthRequestDTO request) {

		this.validateAuthRequestDTO(request);

		if (!this.userService.validUser(request)) {
			throw new RestException("The user/password is not correct", "AUTH-0014", HttpStatus.UNAUTHORIZED);
		}

		final UserDTO user = this.userService.getUserByUsername(request.getUsername());
		final List<UserRoleDTO> userRoles = userRoleService.getAllUserRolesByUser(user.getId());
		List<String> roleList = userRoles.stream().filter(x -> x.getRole() != null).map(x -> x.getRole().getRole())
				.collect(Collectors.toList());

		final Map<String, Object> claims = new HashMap<String, Object>();
		claims.put(SharedConstants.CLAIMS_PASSWORD_CHANGE_REQUIRED, user.isPasswordChangeRequired());
		claims.put(SharedConstants.CLAIMS_USER_ID, user.getId());
		claims.put(SharedConstants.CLAIMS_USER_FULLNAME, user.getFullname());
		claims.put(SharedConstants.CLAIMS_USER_ROLES, roleList);

		final String token = this.jwtService.generateToken(claims, user);

		return token;
	}

	@Override
	public void changePassword(String token, ChangePasswordDTO changePasswordDTO) {
		String username = null;
		Long userId = null;
		try {
			username = this.jwtService.extractUsername(token);
			userId = this.jwtService.extractUserId(token);
		} catch (Exception e) {
			throw new RestException("Token is not valid", "AUTH-0016", HttpStatus.BAD_REQUEST);
		}

		this.validatePasswordChange(username, changePasswordDTO);
		this.userService.changePassword(userId, changePasswordDTO);

	}

	private void validateAuthRequestDTO(AuthRequestDTO request) {
		if (request == null) {
			throw new RestException("Request object not sent", "AUTH-0013", HttpStatus.BAD_REQUEST);
		}

		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			throw new RestException("No user sent", "AUTH-0002", HttpStatus.BAD_REQUEST);
		}

		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			throw new RestException("Invalid password", "AUTH-0005", HttpStatus.BAD_REQUEST);
		}
	}

	private void validatePasswordChange(String username, ChangePasswordDTO changePasswordDTO) {
		if (changePasswordDTO == null) {
			throw new RestException("No information about password change sent", "AUTH-0007", HttpStatus.BAD_REQUEST);
		}

		if (changePasswordDTO.getNewPassword() == null
				|| !changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
			throw new RestException("New password and confirmation password doesn't match", "AUTH-0008",
					HttpStatus.BAD_REQUEST);
		}

		if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getOldPassword())) {
			throw new RestException("New password can not be the old password", "AUTH-0018", HttpStatus.BAD_REQUEST);
		}

		if (!this.pattern.matcher(changePasswordDTO.getNewPassword()).matches()) {
			throw new RestException("Password doesn't meet minimum requirements", "AUTH-0010", HttpStatus.BAD_REQUEST);
		}

		final AuthRequestDTO request = new AuthRequestDTO();
		request.setUsername(username);
		request.setPassword(changePasswordDTO.getOldPassword());

		if (!this.userService.validUser(request)) {
			throw new RestException("The user/password is not correct", "AUTH-0014", HttpStatus.UNAUTHORIZED);
		}

	}

}
