package gracia.marlon.playground.mvc.services;

import gracia.marlon.playground.mvc.dtos.AuthRequestDTO;
import gracia.marlon.playground.mvc.dtos.ChangePasswordDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.dtos.UserWithPasswordDTO;

public interface UserService {

	PagedResponse<UserDTO> getUsers(Integer page, Integer pageSize);

	UserDTO getUserById(Long userId);

	UserDTO getUserByUsername(String username);

	void createUser(UserWithPasswordDTO user);

	void deleteUser(Long userId);

	void changePassword(Long userId, ChangePasswordDTO changePasswordDTO);

	boolean validUser(AuthRequestDTO request);

}
