package gracia.marlon.playground.mvc.dtos;

import java.io.Serializable;

import lombok.Data;

@Data
public class RoleDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String role;

}
