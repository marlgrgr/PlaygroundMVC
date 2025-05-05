package gracia.marlon.playground.mvc.dtos;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserRoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private UserDTO user;

	private RoleDTO role;

}
