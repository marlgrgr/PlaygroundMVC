package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.exception.RestException;

public class AuthenticationServiceTest {

	private final JWTService jwtService;

	private final UserService userService;

	private final UserRoleService userRoleService;

	private final AuthenticationService authenticationService;

	public AuthenticationServiceTest() {
		this.jwtService = Mockito.mock(JWTService.class);
		this.userService = Mockito.mock(UserService.class);
		this.userRoleService = Mockito.mock(UserRoleService.class);
		this.authenticationService = new AuthenticationServiceImpl(this.jwtService, this.userService,
				this.userRoleService);
	}

	@Test
	public void loginSuccessful() {
		AuthRequestDTO request = new AuthRequestDTO();
		request.setUsername("user");
		request.setPassword("user*123");

		UserDTO user = new UserDTO();
		user.setId(1L);
		user.setUsername("user");

		UserRoleDTO userRoleDTO = new UserRoleDTO();
		List<UserRoleDTO> userRoleList = new ArrayList<UserRoleDTO>();
		userRoleList.add(userRoleDTO);

		Mockito.when(this.userService.validUser(request)).thenReturn(true);
		Mockito.when(this.userService.getUserByUsername("user")).thenReturn(user);
		Mockito.when(this.userRoleService.getAllUserRolesByUser(Mockito.anyLong())).thenReturn(userRoleList);
		Mockito.when(this.jwtService.generateToken(Mockito.anyMap(), Mockito.any())).thenReturn("token123");

		assertEquals("token123", this.authenticationService.login(request));

	}

	@Test
	public void loginInvalidRequest() {
		final AuthRequestDTO nullRequest = null;

		RestException restException = assertThrows(RestException.class,
				() -> this.authenticationService.login(nullRequest));

		assertEquals("AUTH-0013", restException.getError().getCode());

		final AuthRequestDTO requestNullUser = new AuthRequestDTO();
		requestNullUser.setPassword("pass");

		restException = assertThrows(RestException.class, () -> this.authenticationService.login(requestNullUser));

		assertEquals("AUTH-0002", restException.getError().getCode());

		final AuthRequestDTO requestEmptyUser = new AuthRequestDTO();
		requestEmptyUser.setUsername("");
		requestEmptyUser.setPassword("pass");

		restException = assertThrows(RestException.class, () -> this.authenticationService.login(requestEmptyUser));

		assertEquals("AUTH-0002", restException.getError().getCode());

		final AuthRequestDTO requestNullPass = new AuthRequestDTO();
		requestNullPass.setUsername("user");

		restException = assertThrows(RestException.class, () -> this.authenticationService.login(requestNullPass));

		assertEquals("AUTH-0005", restException.getError().getCode());

		final AuthRequestDTO requestEmptyPass = new AuthRequestDTO();
		requestEmptyPass.setUsername("user");
		requestEmptyPass.setPassword("");

		restException = assertThrows(RestException.class, () -> this.authenticationService.login(requestEmptyPass));

		assertEquals("AUTH-0005", restException.getError().getCode());

	}

	@Test
	public void loginFailUserValidation() {
		AuthRequestDTO request = new AuthRequestDTO();
		request.setUsername("user");
		request.setPassword("user*123");

		Mockito.when(this.userService.validUser(request)).thenReturn(false);

		RestException restException = assertThrows(RestException.class,
				() -> this.authenticationService.login(request));

		assertEquals("AUTH-0014", restException.getError().getCode());
	}

	@Test
	public void changePasswordSuccessful() {
		String token = "token123";

		Mockito.when(this.jwtService.extractUsername(token)).thenReturn("user");
		Mockito.when(this.jwtService.extractUserId(token)).thenReturn(1L);
		Mockito.when(this.userService.validUser(Mockito.any())).thenReturn(true);
		Mockito.doNothing().when(this.userService).changePassword(Mockito.anyLong(), Mockito.any());

		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setConfirmNewPassword("newPass*123");
		changePasswordDTO.setNewPassword("newPass*123");
		changePasswordDTO.setOldPassword("oldPass");

		this.authenticationService.changePassword(token, changePasswordDTO);

		Mockito.verify(this.userService).changePassword(Mockito.anyLong(), Mockito.any());

	}

	@Test
	public void changePasswordJWTFails() {
		String token = "token123";

		Mockito.when(this.jwtService.extractUsername(token)).thenThrow(new RuntimeException());

		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setConfirmNewPassword("newPass*123");
		changePasswordDTO.setNewPassword("newPass*123");
		changePasswordDTO.setOldPassword("oldPass");

		RestException restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTO));

		assertEquals("AUTH-0016", restException.getError().getCode());

	}

	@Test
	public void changePasswordFailValidations() {
		String token = "token123";

		Mockito.when(this.jwtService.extractUsername(token)).thenReturn("user");
		Mockito.when(this.jwtService.extractUserId(token)).thenReturn(1L);

		final ChangePasswordDTO changePasswordDTONull = null;

		RestException restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTONull));
		assertEquals("AUTH-0007", restException.getError().getCode());

		final ChangePasswordDTO changePasswordDTONullNewPass = new ChangePasswordDTO();
		changePasswordDTONullNewPass.setConfirmNewPassword("newPass*123");
		changePasswordDTONullNewPass.setNewPassword(null);
		changePasswordDTONullNewPass.setOldPassword("oldPass");

		restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTONullNewPass));
		assertEquals("AUTH-0008", restException.getError().getCode());

		final ChangePasswordDTO changePasswordDTONotEquals = new ChangePasswordDTO();
		changePasswordDTONotEquals.setConfirmNewPassword("newPass*1234");
		changePasswordDTONotEquals.setNewPassword("newPass*123");
		changePasswordDTONotEquals.setOldPassword("oldPass");

		restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTONotEquals));
		assertEquals("AUTH-0008", restException.getError().getCode());

		final ChangePasswordDTO changePasswordDTOOldEqualsNew = new ChangePasswordDTO();
		changePasswordDTOOldEqualsNew.setConfirmNewPassword("newPass*123");
		changePasswordDTOOldEqualsNew.setNewPassword("newPass*123");
		changePasswordDTOOldEqualsNew.setOldPassword("newPass*123");

		restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTOOldEqualsNew));
		assertEquals("AUTH-0018", restException.getError().getCode());

		final ChangePasswordDTO changePasswordDTONotValidPass = new ChangePasswordDTO();
		changePasswordDTONotValidPass.setConfirmNewPassword("newPass");
		changePasswordDTONotValidPass.setNewPassword("newPass");
		changePasswordDTONotValidPass.setOldPassword("oldPass");

		restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTONotValidPass));
		assertEquals("AUTH-0010", restException.getError().getCode());

	}

	@Test
	public void changePasswordNotValidUser() {
		String token = "token123";

		Mockito.when(this.jwtService.extractUsername(token)).thenReturn("user");
		Mockito.when(this.jwtService.extractUserId(token)).thenReturn(1L);
		Mockito.when(this.userService.validUser(Mockito.any())).thenReturn(false);

		ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
		changePasswordDTO.setConfirmNewPassword("newPass*123");
		changePasswordDTO.setNewPassword("newPass*123");
		changePasswordDTO.setOldPassword("oldPass");

		RestException restException = assertThrows(RestException.class,
				() -> this.authenticationService.changePassword(token, changePasswordDTO));
		assertEquals("AUTH-0014", restException.getError().getCode());
	}
}
