package gracia.marlon.playground.mvc.dtos;

import lombok.Data;

@Data
public class ChangePasswordDTO {

	private String oldPassword;

	private String newPassword;

	private String confirmNewPassword;

}
