package gracia.marlon.playground.mvc.dtos;

import lombok.Data;

@Data
public class UserWithPasswordDTO {

	private Long id;

	private String username;

	private String password;

	private String fullname;

}
