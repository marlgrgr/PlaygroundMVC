package gracia.marlon.playground.mvc.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.services.MovieReviewService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MovieReviewGraphQLController {

	private final MovieReviewService movieReviewService;

	@QueryMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public PagedResponse<MovieReviewDTO> getMovieReviewResponseDTO(@Argument Integer page, @Argument Integer pageSize) {
		final PagedResponse<MovieReviewDTO> pagedResponse = this.movieReviewService.getMovieReviews(page, pageSize);
		return pagedResponse;
	}

	@QueryMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public MovieReviewDTO getMovieReviewResponseDTOByID(@Argument String movieReviewId) {
		final MovieReviewDTO movieReviewResponseDTO = this.movieReviewService.getMovieReviewByID(movieReviewId);
		return movieReviewResponseDTO;
	}

	@QueryMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public PagedResponse<MovieReviewDTO> getMovieReviewResponseDTOByMovieID(@Argument Long movieId,
			@Argument Integer page, @Argument Integer pageSize) {
		final PagedResponse<MovieReviewDTO> pagedResponse = this.movieReviewService.getMovieReviewsByMovie(movieId,
				page, pageSize);
		return pagedResponse;
	}

	@MutationMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public Boolean createMovieReview(@Argument("movieReviewDTO") MovieReviewDTO movieReviewDTO) {
		this.movieReviewService.saveReview(movieReviewDTO);
		return true;
	}

}