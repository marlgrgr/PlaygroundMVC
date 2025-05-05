package gracia.marlon.playground.mvc.services;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;

public interface MovieService {

	void requestForloadMovies();

	PagedResponse<MovieDTO> getMovieList(Integer page, Integer pageSize);

}
