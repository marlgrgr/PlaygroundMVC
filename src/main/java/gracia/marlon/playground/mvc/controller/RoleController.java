package gracia.marlon.playground.mvc.controller;

import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.exception.ApiError;
import gracia.marlon.playground.mvc.services.RoleService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Operations to get the roles from the app")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

	private final RoleService roleService;

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("/roles")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieve roles"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	public List<RoleDTO> getRoleList() {
		final List<RoleDTO> roleList = this.roleService.getRoles();
		return roleList;
	}

}
