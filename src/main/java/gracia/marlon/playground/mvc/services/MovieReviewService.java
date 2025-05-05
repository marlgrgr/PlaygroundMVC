package gracia.marlon.playground.mvc.services;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;

public interface MovieReviewService {

	PagedResponse<MovieReviewDTO> getMovieReviews(Integer page, Integer pageSize);

	PagedResponse<MovieReviewDTO> getMovieReviewsByMovie(Long movieId, Integer page, Integer pageSize);

	MovieReviewDTO getMovieReviewByID(String id);

	void saveReview(MovieReviewDTO movieReviewDTO);

}
