package gracia.marlon.playground.mvc.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.exception.ApiError;
import gracia.marlon.playground.mvc.services.MovieReviewService;
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
@Tag(name = "Movie Review", description = "Operations to retrieve the reviews from movies and to create new reviews")
@SecurityRequirement(name = "bearerAuth")
public class MovieReviewController {

	private final MovieReviewService movieReviewService;

	@GetMapping("/movieReview")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieve movie reviews"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	public PagedResponse<MovieReviewDTO> getMovieReviewResponseDTO(
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize) {
		final PagedResponse<MovieReviewDTO> pagedResponse = this.movieReviewService.getMovieReviews(page, pageSize);
		return pagedResponse;
	}

	@GetMapping("/movieReview/{movieReviewId}")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieve movie review"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "404", description = "Movie review not found.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	public MovieReviewDTO getMovieReviewResponseDTOByID(@PathVariable String movieReviewId) {
		final MovieReviewDTO movieReviewResponseDTO = this.movieReviewService.getMovieReviewByID(movieReviewId);
		return movieReviewResponseDTO;
	}

	@GetMapping("/movieReview/movie/{movieId}")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Successfully retrieve movie reviews"),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	public PagedResponse<MovieReviewDTO> getMovieReviewResponseDTOByMovieID(
			@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize, @PathVariable Long movieId) {
		final PagedResponse<MovieReviewDTO> pagedResponse = this.movieReviewService.getMovieReviewsByMovie(movieId,
				page, pageSize);
		return pagedResponse;
	}

	@PostMapping("/movieReview")
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Successfully movie review created"),
			@ApiResponse(responseCode = "400", description = "validations are not met", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized: authentication is required.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
			@ApiResponse(responseCode = "403", description = "Access denied: you do not have the required permissions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))) })
	@ResponseStatus(HttpStatus.CREATED)
	public void createMovieReview(@RequestBody MovieReviewDTO movieReviewDTO) {
		this.movieReviewService.saveReview(movieReviewDTO);
	}
}
