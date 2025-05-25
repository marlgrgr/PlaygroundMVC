package gracia.marlon.playground.mvc.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.services.MovieService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MovieGraphQLController {

	private final MovieService movieService;

	@MutationMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public Boolean loadMovies() {
		this.movieService.requestForloadMovies();
		return true;
	}

	@QueryMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public PagedResponse<MovieDTO> getMovieDetailsResponseDTO(@Argument Integer page, @Argument Integer pageSize) {
		final PagedResponse<MovieDTO> pagedResponse = this.movieService.getMovieList(page, pageSize);
		return pagedResponse;
	}

	@QueryMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public MovieDTO getMovieById(@Argument Long movieId) {
		final MovieDTO movieDTO = this.movieService.getMovieById(movieId);
		return movieDTO;
	}
}