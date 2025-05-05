package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.MovieReviewMapper;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.model.MovieReview;
import gracia.marlon.playground.mvc.repository.MovieRepository;
import gracia.marlon.playground.mvc.repository.MovieReviewRepository;

public class MovieReviewServiceTest {

	private final MovieReviewRepository movieReviewRepository;

	private final MovieRepository movieRepository;

	private final CacheService cacheService;

	private final MovieReviewMapper movieReviewMapper;

	private final MovieReviewService movieReviewService;

	public MovieReviewServiceTest() {
		this.movieReviewRepository = Mockito.mock(MovieReviewRepository.class);
		this.movieRepository = Mockito.mock(MovieRepository.class);
		this.cacheService = Mockito.mock(CacheService.class);
		this.movieReviewMapper = Mockito.mock(MovieReviewMapper.class);
		this.movieReviewService = new MovieReviewServiceImpl(this.movieReviewRepository, this.movieRepository,
				this.cacheService, this.movieReviewMapper);
	}

	@Test
	public void getMovieReviewsSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		MovieReview movieReview = new MovieReview();
		movieReview.setId("_id1");
		List<MovieReview> content = new ArrayList<MovieReview>();
		content.add(movieReview);

		Page<MovieReview> movieReviewPage = new PageImpl<MovieReview>(content, pageable, content.size());

		MovieReviewDTO movieReviewDTO = new MovieReviewDTO();
		movieReviewDTO.setId("_id1");

		Mockito.when(this.movieReviewRepository.findAll(Mockito.any(Pageable.class))).thenReturn(movieReviewPage);
		Mockito.when(this.movieReviewMapper.toDto(Mockito.any())).thenReturn(movieReviewDTO);

		PagedResponse<MovieReviewDTO> pagedResponse = this.movieReviewService.getMovieReviews(1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals("_id1", pagedResponse.getResults().getFirst().getId());
	}

	@Test
	public void getMovieReviewsByMovieSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		MovieReview movieReview = new MovieReview();
		movieReview.setId("_id1");
		List<MovieReview> content = new ArrayList<MovieReview>();
		content.add(movieReview);

		Page<MovieReview> movieReviewPage = new PageImpl<MovieReview>(content, pageable, content.size());

		MovieReviewDTO movieReviewDTO = new MovieReviewDTO();
		movieReviewDTO.setId("_id1");

		Mockito.when(this.movieReviewRepository.findByMovieId(Mockito.anyLong(), Mockito.any(Pageable.class)))
				.thenReturn(movieReviewPage);
		Mockito.when(this.movieReviewMapper.toDto(Mockito.any())).thenReturn(movieReviewDTO);

		PagedResponse<MovieReviewDTO> pagedResponse = this.movieReviewService.getMovieReviewsByMovie(1L, 1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals("_id1", pagedResponse.getResults().getFirst().getId());
	}

	@Test
	public void getMovieReviewByIDSuccessful() {
		MovieReview movieReview = new MovieReview();
		movieReview.setId("_id1");
		Optional<MovieReview> optionalMovieReview = Optional.of(movieReview);

		MovieReviewDTO movieReviewDTO = new MovieReviewDTO();
		movieReviewDTO.setId("_id1");

		Mockito.when(this.movieReviewRepository.findById(Mockito.anyString())).thenReturn(optionalMovieReview);
		Mockito.when(this.movieReviewMapper.toDto(Mockito.any())).thenReturn(movieReviewDTO);

		MovieReviewDTO movieReviewDTOResponse = this.movieReviewService.getMovieReviewByID("_id1");

		assertEquals("_id1", movieReviewDTOResponse.getId());
	}

	@Test
	public void getMovieReviewByIDEmpty() {
		Optional<MovieReview> optionalMovieReview = Optional.empty();

		Mockito.when(this.movieReviewRepository.findById(Mockito.anyString())).thenReturn(optionalMovieReview);

		RestException restException = assertThrows(RestException.class,
				() -> this.movieReviewService.getMovieReviewByID("_id1"));

		assertEquals("MOVIE-0006", restException.getError().getCode());
	}

	@Test
	public void saveReviewSuccessful() {
		MovieReviewDTO movieReviewDTO = new MovieReviewDTO();
		movieReviewDTO.setId("_id1");
		movieReviewDTO.setMovieId(1L);
		movieReviewDTO.setReview("was good");
		movieReviewDTO.setScore(4.5);

		MovieReview movieReview = new MovieReview();
		Movie movie = new Movie();
		Optional<Movie> optinalMovie = Optional.of(movie);

		Mockito.when(this.movieReviewMapper.toEntity(Mockito.any())).thenReturn(movieReview);
		Mockito.when(this.movieRepository.findById(Mockito.anyLong())).thenReturn(optinalMovie);
		Mockito.when(this.movieReviewRepository.save(Mockito.any())).thenReturn(null);
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());

		this.movieReviewService.saveReview(movieReviewDTO);

		Mockito.verify(this.cacheService, times(3)).evictCache(Mockito.anyString());

	}

	@Test
	public void saveReviewValidations() {
		RestException restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(null));
		assertEquals("MOVIE-0001", restException.getError().getCode());

		MovieReviewDTO movieReviewDTO = new MovieReviewDTO();
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0002", restException.getError().getCode());

		movieReviewDTO.setMovieId(1L);
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0003", restException.getError().getCode());

		movieReviewDTO.setReview("");
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0003", restException.getError().getCode());

		movieReviewDTO.setReview("good movie");
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0004", restException.getError().getCode());

		movieReviewDTO.setScore(-1.0);
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0004", restException.getError().getCode());

		movieReviewDTO.setScore(6.0);
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0004", restException.getError().getCode());

		movieReviewDTO.setScore(4.5);
		Mockito.when(this.movieRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
		restException = assertThrows(RestException.class, () -> this.movieReviewService.saveReview(movieReviewDTO));
		assertEquals("MOVIE-0005", restException.getError().getCode());

	}

}
