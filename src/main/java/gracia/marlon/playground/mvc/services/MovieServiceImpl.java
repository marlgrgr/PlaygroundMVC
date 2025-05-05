package gracia.marlon.playground.mvc.services;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.mapper.MovieMapper;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.queue.QueuePublisherService;
import gracia.marlon.playground.mvc.repository.MovieRepository;
import gracia.marlon.playground.mvc.util.PageableUtil;
import gracia.marlon.playground.mvc.util.SharedConstants;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieServiceImpl implements MovieService {

	private final CacheService cacheService;

	private final RedissonClient redissonClient;

	private final QueuePublisherService queuePublisherService;

	private final MovieRepository movieRepository;

	private final MovieMapper movieMapper;

	private final String LOAD_KEY = "LOAD_MOVIE_STATUS";

	private final String STATUS_NOT_AVAILABLE = "NOT_AVAILABLE";

	private final String loadMoviesQueueName;

	private final int MAX_WAIT_FOR_LOCK_TIME = 10;

	private final int MAX_TIME_WITH_LOCK = 60;

	private final int TTL_SPECIAL_CACHE = 600;

	public MovieServiceImpl(CacheService cacheService, RedissonClient redissonClient,
			QueuePublisherService queuePublisherService, MovieRepository movieRepository, MovieMapper movieMapper,
			Environment env) {
		this.cacheService = cacheService;
		this.redissonClient = redissonClient;
		this.queuePublisherService = queuePublisherService;
		this.movieRepository = movieRepository;
		this.movieMapper = movieMapper;
		this.loadMoviesQueueName = env.getProperty("sqs.queue.loadMovies.name", "");

	}

	@Override
	public void requestForloadMovies() {
		String loadMovieStatus = this.cacheService.getFromSpecialCache(LOAD_KEY);

		if (loadMovieStatus != null && loadMovieStatus.equals(STATUS_NOT_AVAILABLE)) {
			return;
		}

		final RLock lock = this.redissonClient.getLock("load-movie-lock");

		try {
			final boolean isLocked = lock.tryLock(MAX_WAIT_FOR_LOCK_TIME, MAX_TIME_WITH_LOCK, TimeUnit.SECONDS);
			if (isLocked) {
				loadMovieStatus = this.cacheService.getFromSpecialCache(LOAD_KEY);

				if (loadMovieStatus != null && loadMovieStatus.equals(STATUS_NOT_AVAILABLE)) {
					return;
				}

				this.queuePublisherService.publish(SharedConstants.LOAD_MESSAGE, loadMoviesQueueName);

				this.cacheService.putInSpecialCacheWithTTL(LOAD_KEY, STATUS_NOT_AVAILABLE, TTL_SPECIAL_CACHE);
			}
		} catch (Exception e) {
			log.error("An error occurred creating the request for movies to be load", e);
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}

	}

	@Override
	@Cacheable(cacheNames = "MovieService_getMovieList", key = "#page + '-' + #pageSize", unless = "#result == null")
	public PagedResponse<MovieDTO> getMovieList(Integer page, Integer pageSize) {
		final Sort sort = Sort.by(Sort.Order.desc("createOn"), Sort.Order.desc("popularity"));
		final Pageable pageable = PageableUtil.getPageable(page, pageSize, sort);

		final Page<Movie> moviePage = this.movieRepository.findAll(pageable);

		final List<MovieDTO> movieDTOList = moviePage.get().map(this.movieMapper::toDto).collect(Collectors.toList());

		final PagedResponse<MovieDTO> pagedResponse = PageableUtil.getPagedResponse(moviePage, movieDTOList);

		return pagedResponse;
	}

}
