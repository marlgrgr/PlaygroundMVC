package gracia.marlon.playground.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.exception.ApiError;
import gracia.marlon.playground.mvc.services.MovieService;
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
@Tag(name = "Movies", description = "Operations to load the movies and retrieve them")
@SecurityRequirement(name = "bearerAuth")
public class MovieController {

	private final MovieService movieService;

	@PostMapping("/movie/load")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiResponses(value = { @ApiResponse(responseCode = "202", description = "Request accepted"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	public void loadMovies() {
		this.movieService.requestForloadMovies();
	}

	@GetMapping("/movie")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieve movies"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	public PagedResponse<MovieDTO> getMovieDetailsResponseDTO(
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		final PagedResponse<MovieDTO> pagedResponse = this.movieService.getMovieList(page, pageSize);
		return pagedResponse;
	}

}
