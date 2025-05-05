package gracia.marlon.playground.mvc.services;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;

public interface AuthenticationService {

	String login(AuthRequestDTO request);

	void changePassword(String token, ChangePasswordDTO changePasswordDTO);

}
