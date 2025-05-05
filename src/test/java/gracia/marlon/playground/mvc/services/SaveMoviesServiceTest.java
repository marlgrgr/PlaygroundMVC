package gracia.marlon.playground.mvc.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import gracia.marlon.playground.mvc.dtos.MovieDetailsDTO;
import gracia.marlon.playground.mvc.dtos.MovieDetailsResponseDTO;
import gracia.marlon.playground.mvc.mapper.MovieDetailsMapper;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.repository.MovieRepository;

public class SaveMoviesServiceTest {

	private final MovieRepository movieRepository;

	private final CacheService cacheService;

	private final MessageProducerService messageProducerService;

	private final MovieDetailsMapper movieDetailsMapper;

	private final SaveMoviesService saveMoviesService;

	public SaveMoviesServiceTest() {
		this.movieRepository = Mockito.mock(MovieRepository.class);
		this.cacheService = Mockito.mock(CacheService.class);
		this.messageProducerService = Mockito.mock(MessageProducerService.class);
		this.movieDetailsMapper = Mockito.mock(MovieDetailsMapper.class);
		this.saveMoviesService = new SaveMoviesServiceImpl(this.movieRepository, this.cacheService,
				this.messageProducerService, this.movieDetailsMapper);
	}

	@Test
	public void saveMovieListSuccessful() {
		MovieDetailsDTO movieDetailsDTO = new MovieDetailsDTO();

		List<MovieDetailsDTO> results = new ArrayList<MovieDetailsDTO>();
		results.add(movieDetailsDTO);

		MovieDetailsResponseDTO movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		movieDetailsResponseDTO.setResults(results);

		Movie movie = new Movie();

		Mockito.when(this.movieDetailsMapper.toEntity(Mockito.any())).thenReturn(movie);
		Mockito.when(this.movieRepository.saveAll(Mockito.any())).thenReturn(null);
		Mockito.doNothing().when(this.cacheService).evictCache(Mockito.anyString());
		Mockito.doNothing().when(this.messageProducerService).publishMessage(Mockito.anyString(), Mockito.anyString());

		this.saveMoviesService.saveMovieList(movieDetailsResponseDTO);

		Mockito.verify(this.messageProducerService).publishMessage(Mockito.anyString(), Mockito.anyString());

	}
	
	@Test
	public void saveMovieListNotValid() {
		this.saveMoviesService.saveMovieList(null);
		Mockito.verify(this.messageProducerService, Mockito.never()).publishMessage(Mockito.anyString(), Mockito.anyString());
		
		MovieDetailsResponseDTO movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		this.saveMoviesService.saveMovieList(movieDetailsResponseDTO);
		Mockito.verify(this.messageProducerService, Mockito.never()).publishMessage(Mockito.anyString(), Mockito.anyString());
		
		movieDetailsResponseDTO.setResults(new ArrayList<MovieDetailsDTO>());
		this.saveMoviesService.saveMovieList(movieDetailsResponseDTO);
		Mockito.verify(this.messageProducerService, Mockito.never()).publishMessage(Mockito.anyString(), Mockito.anyString());

	}
	
	@Test
	public void saveMovieListException() {
		
		MovieDetailsDTO movieDetailsDTO = new MovieDetailsDTO();

		List<MovieDetailsDTO> results = new ArrayList<MovieDetailsDTO>();
		results.add(movieDetailsDTO);

		MovieDetailsResponseDTO movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		movieDetailsResponseDTO.setResults(results);

		Movie movie = new Movie();

		Mockito.when(this.movieDetailsMapper.toEntity(Mockito.any())).thenReturn(movie);
		Mockito.when(this.movieRepository.saveAll(Mockito.any())).thenThrow(new RuntimeException());
		
		this.saveMoviesService.saveMovieList(movieDetailsResponseDTO);
		Mockito.verify(this.messageProducerService, Mockito.never()).publishMessage(Mockito.anyString(), Mockito.anyString());

	}
}
