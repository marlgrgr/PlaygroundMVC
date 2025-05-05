package gracia.marlon.playground.mvc.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.MovieReviewMapper;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.model.MovieReview;
import gracia.marlon.playground.mvc.repository.MovieRepository;
import gracia.marlon.playground.mvc.repository.MovieReviewRepository;
import gracia.marlon.playground.mvc.util.PageableUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieReviewServiceImpl implements MovieReviewService {

	private final MovieReviewRepository movieReviewRepository;

	private final MovieRepository movieRepository;

	private final CacheService cacheService;

	private final MovieReviewMapper movieReviewMapper;

	private final double MIN_SCORE = 0;

	private final double MAX_SCORE = 5;

	@Override
	@Cacheable(cacheNames = "MovieReviewService_getMovieReviews", key = "#page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<MovieReviewDTO> getMovieReviews(Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("createOn"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<MovieReview> movieReviewPage = this.movieReviewRepository.findAll(pageable);

		final List<MovieReviewDTO> movieReviewDTOList = movieReviewPage.get().map(this.movieReviewMapper::toDto)
				.collect(Collectors.toList());

		final PagedResponse<MovieReviewDTO> pagedResponse = PageableUtil.getPagedResponse(movieReviewPage,
				movieReviewDTOList);

		return pagedResponse;
	}

	@Override
	@Cacheable(cacheNames = "MovieReviewService_getMovieReviewsByMovie", key = "#movieId + '-' + #page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<MovieReviewDTO> getMovieReviewsByMovie(Long movieId, Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("createOn"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<MovieReview> movieReviewPage = this.movieReviewRepository.findByMovieId(movieId, pageable);

		final List<MovieReviewDTO> movieReviewDTOList = movieReviewPage.get().map(this.movieReviewMapper::toDto)
				.collect(Collectors.toList());

		final PagedResponse<MovieReviewDTO> pagedResponse = PageableUtil.getPagedResponse(movieReviewPage,
				movieReviewDTOList);

		return pagedResponse;
	}

	@Override
	@Cacheable(cacheNames = "MovieReviewService_getMovieReviewByID", key = "#id", unless = "#result == null")
	public MovieReviewDTO getMovieReviewByID(String id) {
		Optional<MovieReview> movieReview = this.movieReviewRepository.findById(id);

		if (movieReview.isEmpty()) {
			throw new RestException("The movie review doesn't exist", "MOVIE-0006", HttpStatus.NOT_FOUND);
		}

		MovieReviewDTO movieReviewDTO = this.movieReviewMapper.toDto(movieReview.get());
		return movieReviewDTO;
	}

	@Override
	public void saveReview(MovieReviewDTO movieReviewDTO) {
		this.validateMovieReviewDTO(movieReviewDTO);
		movieReviewDTO.setCreateOn(new Date());
		this.movieReviewRepository.save(this.movieReviewMapper.toEntity(movieReviewDTO));
		this.evictMovieReviewCaches();
	}

	private void validateMovieReviewDTO(MovieReviewDTO movieReviewDTO) {

		if (movieReviewDTO == null) {
			throw new RestException("No movie review sent", "MOVIE-0001", HttpStatus.BAD_REQUEST);
		}

		if (movieReviewDTO.getMovieId() == null) {
			throw new RestException("No movie sent", "MOVIE-0002", HttpStatus.BAD_REQUEST);
		}

		if (movieReviewDTO.getReview() == null || movieReviewDTO.getReview().trim().isEmpty()) {
			throw new RestException("The review can not be empty", "MOVIE-0003", HttpStatus.BAD_REQUEST);
		}

		if (movieReviewDTO.getScore() == null || movieReviewDTO.getScore() < MIN_SCORE
				|| movieReviewDTO.getScore() > MAX_SCORE) {
			throw new RestException("The score is not valid", "MOVIE-0004", HttpStatus.BAD_REQUEST);
		}

		final Optional<Movie> movie = this.movieRepository.findById(movieReviewDTO.getMovieId());
		if (movie.isEmpty()) {
			throw new RestException("The movie doesn't exist", "MOVIE-0005", HttpStatus.NOT_FOUND);
		}
	}

	private void evictMovieReviewCaches() {
		this.cacheService.evictCache("MovieReviewService_getMovieReviews");
		this.cacheService.evictCache("MovieReviewService_getMovieReviewsByMovie");
		this.cacheService.evictCache("MovieReviewService_getMovieReviewByID");
	}

}
