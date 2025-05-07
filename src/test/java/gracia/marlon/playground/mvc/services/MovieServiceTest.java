package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.exception.RestException;
import gracia.marlon.playground.mvc.mapper.MovieMapper;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.queue.QueuePublisherService;
import gracia.marlon.playground.mvc.repository.MovieRepository;

public class MovieServiceTest {

	private final CacheService cacheService;

	private final RedissonClient redissonClient;

	private final QueuePublisherService queuePublisherService;

	private final MovieRepository movieRepository;

	private final MovieMapper movieMapper;

	private final Environment env;

	private final MovieService movieService;

	private final String STATUS_NOT_AVAILABLE = "NOT_AVAILABLE";

	public MovieServiceTest() {
		this.cacheService = Mockito.mock(CacheService.class);
		this.redissonClient = Mockito.mock(RedissonClient.class);
		this.queuePublisherService = Mockito.mock(QueuePublisherService.class);
		this.movieRepository = Mockito.mock(MovieRepository.class);
		this.movieMapper = Mockito.mock(MovieMapper.class);
		this.env = Mockito.mock(Environment.class);
		Mockito.when(this.env.getProperty("sqs.queue.loadMovies.name", "")).thenReturn("loadMovies.name");
		this.movieService = new MovieServiceImpl(this.cacheService, this.redissonClient, this.queuePublisherService,
				this.movieRepository, this.movieMapper, this.env);
	}

	@Test
	public void requestForloadMoviesSuccessful() throws InterruptedException {
		RLock lock = Mockito.mock(RLock.class);

		Mockito.when(this.cacheService.getFromSpecialCache(Mockito.anyString())).thenReturn(null);
		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenReturn(true);
		Mockito.doNothing().when(this.queuePublisherService).publish(Mockito.anyString(), Mockito.anyString());
		Mockito.doNothing().when(this.cacheService).putInSpecialCacheWithTTL(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt());
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(true);
		Mockito.doNothing().when(lock).unlock();

		this.movieService.requestForloadMovies();

		Mockito.verify(this.cacheService).putInSpecialCacheWithTTL(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt());
	}

	@Test
	public void requestForloadMoviesSuccessfulWithAvailable() throws InterruptedException {
		RLock lock = Mockito.mock(RLock.class);

		Mockito.when(this.cacheService.getFromSpecialCache(Mockito.anyString())).thenReturn("AVAILABLE");
		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenReturn(true);
		Mockito.doNothing().when(this.queuePublisherService).publish(Mockito.anyString(), Mockito.anyString());
		Mockito.doNothing().when(this.cacheService).putInSpecialCacheWithTTL(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt());
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(true);
		Mockito.doNothing().when(lock).unlock();

		this.movieService.requestForloadMovies();

		Mockito.verify(this.cacheService).putInSpecialCacheWithTTL(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt());
	}

	@Test
	public void requestForloadMoviesNotComplete() throws InterruptedException {
		Mockito.when(this.cacheService.getFromSpecialCache(Mockito.anyString())).thenReturn(STATUS_NOT_AVAILABLE);
		this.movieService.requestForloadMovies();
		Mockito.verify(this.cacheService, Mockito.never()).putInSpecialCacheWithTTL(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyInt());

		RLock lock = Mockito.mock(RLock.class);
		Mockito.when(this.cacheService.getFromSpecialCache(Mockito.anyString())).thenReturn(null, STATUS_NOT_AVAILABLE);
		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenReturn(true);
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(true);
		Mockito.doNothing().when(lock).unlock();

		this.movieService.requestForloadMovies();

		Mockito.verify(this.cacheService, Mockito.never()).putInSpecialCacheWithTTL(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyInt());
	}

	@Test
	public void requestForloadMoviesException() throws InterruptedException {
		RLock lock = Mockito.mock(RLock.class);

		Mockito.when(this.cacheService.getFromSpecialCache(Mockito.anyString())).thenReturn(null);
		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class)))
				.thenThrow(new RuntimeException());
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(false);

		this.movieService.requestForloadMovies();

		Mockito.verify(this.cacheService, Mockito.never()).putInSpecialCacheWithTTL(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyInt());
	}

	@Test
	public void requestForloadMoviesNotLocked() throws InterruptedException {
		RLock lock = Mockito.mock(RLock.class);

		Mockito.when(this.cacheService.getFromSpecialCache(Mockito.anyString())).thenReturn(null);
		Mockito.when(this.redissonClient.getLock(Mockito.anyString())).thenReturn(lock);
		Mockito.when(lock.tryLock(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(TimeUnit.class))).thenReturn(false);
		Mockito.when(lock.isHeldByCurrentThread()).thenReturn(false);

		this.movieService.requestForloadMovies();

		Mockito.verify(this.cacheService, Mockito.never()).putInSpecialCacheWithTTL(Mockito.anyString(),
				Mockito.anyString(), Mockito.anyInt());
	}

	@Test
	public void getMovieListSuccessful() {
		Pageable pageable = PageRequest.of(0, 10);

		Movie movie = new Movie();
		movie.setId(1L);
		List<Movie> content = new ArrayList<Movie>();
		content.add(movie);

		Page<Movie> moviePage = new PageImpl<Movie>(content, pageable, content.size());

		MovieDTO movieDTO = new MovieDTO();
		movieDTO.setId(1L);

		Mockito.when(this.movieRepository.findAll(Mockito.any(Pageable.class))).thenReturn(moviePage);
		Mockito.when(this.movieMapper.toDto(Mockito.any())).thenReturn(movieDTO);

		PagedResponse<MovieDTO> pagedResponse = this.movieService.getMovieList(1, 10);

		assertEquals(1, pagedResponse.getPage());
		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals(1L, pagedResponse.getResults().getFirst().getId());
	}

	@Test
	public void getMovieByIdSuccessful() {
		Movie movie = new Movie();
		movie.setId(1L);
		Optional<Movie> optionalMovie = Optional.of(movie);

		MovieDTO movieDTO = new MovieDTO();
		movieDTO.setId(1L);

		Mockito.when(this.movieRepository.findById(Mockito.anyLong())).thenReturn(optionalMovie);
		Mockito.when(this.movieMapper.toDto(Mockito.any())).thenReturn(movieDTO);

		MovieDTO response = this.movieService.getMovieById(1L);

		assertEquals(1L, response.getId());
	}

	@Test
	public void getMovieByIdNotExisting() {
		RestException restException = assertThrows(RestException.class, () -> this.movieService.getMovieById(null));
		assertEquals("MOVIE-0005", restException.getError().getCode());

		Optional<Movie> optionalMovie = Optional.empty();

		Mockito.when(this.movieRepository.findById(Mockito.anyLong())).thenReturn(optionalMovie);

		restException = assertThrows(RestException.class, () -> this.movieService.getMovieById(1L));
		assertEquals("MOVIE-0005", restException.getError().getCode());
	}

}
