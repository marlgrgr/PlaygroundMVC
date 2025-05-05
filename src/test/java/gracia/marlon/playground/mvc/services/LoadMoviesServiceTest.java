package gracia.marlon.playground.mvc.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import gracia.marlon.playground.mvc.dtos.GenreDTO;
import gracia.marlon.playground.mvc.dtos.GenreResponseDTO;
import gracia.marlon.playground.mvc.dtos.MovieDetailsDTO;
import gracia.marlon.playground.mvc.dtos.MovieDetailsResponseDTO;

public class LoadMoviesServiceTest {

	private final RestTemplate restTemplate;

	private final Environment env;

	private LoadMoviesService loadMoviesService;

	public LoadMoviesServiceTest() {
		this.restTemplate = Mockito.mock(RestTemplate.class);
		this.env = Mockito.mock(Environment.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesSuccessful() {

		final SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		MovieDetailsResponseDTO movieDetailsResponseDTO = this.getMovieDetailsResponseDTO();
		GenreResponseDTO genreResponseDTO = this.getGenreResponseDTO();

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);
		ResponseEntity<GenreResponseDTO> genreResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(genreResponse.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(genreResponse.getBody()).thenReturn(genreResponseDTO);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		Mockito.doNothing().when(saveMoviesService).saveMovieList(Mockito.any());

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(2, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());
	}

	@Test
	public void loadMoviesValidURLParams() {
		final SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		Mockito.when(this.env.getProperty("api.movie.baseUrl", "")).thenReturn("");

		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);
		this.loadMoviesService.loadMovies();
		Mockito.verify(saveMoviesService, never()).saveMovieList(Mockito.any());

		Mockito.when(this.env.getProperty("api.movie.baseUrl", "")).thenReturn("baseUrl");
		Mockito.when(this.env.getProperty("api.movie.discover.path", "")).thenReturn("");

		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);
		this.loadMoviesService.loadMovies();
		Mockito.verify(saveMoviesService, never()).saveMovieList(Mockito.any());

		Mockito.when(this.env.getProperty("api.movie.discover.path", "")).thenReturn("discover.path");
		Mockito.when(this.env.getProperty("api.movie.apiKey", "")).thenReturn("");

		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);
		this.loadMoviesService.loadMovies();
		Mockito.verify(saveMoviesService, never()).saveMovieList(Mockito.any());

		Mockito.when(this.env.getProperty("api.movie.apiKey", "")).thenReturn("apiKey");
		Mockito.when(this.env.getProperty("api.movie.genre.path", "")).thenReturn("");

		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);
		this.loadMoviesService.loadMovies();
		Mockito.verify(saveMoviesService, never()).saveMovieList(Mockito.any());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesMovieDetailResponse() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(null);

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		MovieDetailsResponseDTO movieDetailsResponseDTO = captor.getValue();

		assertEquals(null, movieDetailsResponseDTO);

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);

		this.loadMoviesService.loadMovies();
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(null, movieDetailsResponseDTO);

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(null);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(null, movieDetailsResponseDTO);

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		Mockito.when(response.getStatusCode()).thenReturn(null);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		this.loadMoviesService.loadMovies();
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(null, movieDetailsResponseDTO);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesMovieDetailValidation() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		MovieDetailsResponseDTO movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		movieDetailsResponseDTO.setResults(null);

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(null, movieDetailsResponseDTO.getResults());

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		List<MovieDetailsDTO> results = new ArrayList<MovieDetailsDTO>();
		movieDetailsResponseDTO.setResults(results);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);

		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(0, movieDetailsResponseDTO.getResults().size());

	}

	@Test
	public void loadMoviesExceptionAtRestCall() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(this.restTemplate, saveMoviesService, this.env);

		Mockito.when(this.restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenThrow(new RuntimeException());

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		MovieDetailsResponseDTO movieDetailsResponseDTO = captor.getValue();

		assertEquals(null, movieDetailsResponseDTO);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesValidGenresList() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		RestTemplate localRestTemplate = Mockito.mock(RestTemplate.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		List<Integer> genreIds = null;

		MovieDetailsDTO movieDetailsDTO = new MovieDetailsDTO();
		movieDetailsDTO.setId(1L);
		movieDetailsDTO.setGenreIds(genreIds);

		List<MovieDetailsDTO> results = new ArrayList<MovieDetailsDTO>();
		results.add(movieDetailsDTO);

		MovieDetailsResponseDTO movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		movieDetailsResponseDTO.setResults(results);

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		movieDetailsDTO.setGenreIds(new ArrayList<Integer>());
		results = new ArrayList<MovieDetailsDTO>();
		results.add(movieDetailsDTO);

		movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		movieDetailsResponseDTO.setResults(results);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);

		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

		this.loadMoviesService.loadMovies();

	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesWithoutMissingGenres() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		RestTemplate localRestTemplate = Mockito.mock(RestTemplate.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		MovieDetailsResponseDTO movieDetailsResponseDTO = this.getMovieDetailsResponseDTO();
		GenreResponseDTO genreResponseDTO = this.getGenreResponseDTO();

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);
		ResponseEntity<GenreResponseDTO> genreResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(genreResponse.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(genreResponse.getBody()).thenReturn(genreResponseDTO);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		this.loadMoviesService.loadMovies();

		Mockito.verify(localRestTemplate).exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class));

		localRestTemplate = Mockito.mock(RestTemplate.class);

		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		this.loadMoviesService.loadMovies();

		Mockito.verify(localRestTemplate, never()).exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesGenreResponse() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		RestTemplate localRestTemplate = Mockito.mock(RestTemplate.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		MovieDetailsResponseDTO movieDetailsResponseDTO = this.getMovieDetailsResponseDTO();

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(null);

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		ResponseEntity<GenreResponseDTO> genreResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(genreResponse.getStatusCode()).thenReturn(null);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		Mockito.when(genreResponse.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		Mockito.when(genreResponse.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(genreResponse.getBody()).thenReturn(null);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesGenreResponseValidation() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		RestTemplate localRestTemplate = Mockito.mock(RestTemplate.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		MovieDetailsResponseDTO movieDetailsResponseDTO = this.getMovieDetailsResponseDTO();
		GenreResponseDTO genreResponseDTO = this.getGenreResponseDTO();
		genreResponseDTO.setGenres(null);

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);
		ResponseEntity<GenreResponseDTO> genreResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(genreResponse.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(genreResponse.getBody()).thenReturn(genreResponseDTO);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenReturn(genreResponse);

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

		saveMoviesService = Mockito.mock(SaveMoviesService.class);
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		genreResponseDTO.setGenres(new ArrayList<GenreDTO>());

		Mockito.when(genreResponse.getBody()).thenReturn(genreResponseDTO);

		this.loadMoviesService.loadMovies();

		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadMoviesExceptionGenres() {
		SaveMoviesService saveMoviesService = Mockito.mock(SaveMoviesService.class);
		RestTemplate localRestTemplate = Mockito.mock(RestTemplate.class);
		this.MockEnvProperties();
		this.loadMoviesService = new LoadMoviesServiceImpl(localRestTemplate, saveMoviesService, this.env);

		MovieDetailsResponseDTO movieDetailsResponseDTO = this.getMovieDetailsResponseDTO();

		ResponseEntity<MovieDetailsResponseDTO> response = Mockito.mock(ResponseEntity.class);

		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);
		Mockito.when(response.getBody()).thenReturn(movieDetailsResponseDTO);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(MovieDetailsResponseDTO.class))).thenReturn(response);
		Mockito.when(localRestTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(),
				Mockito.eq(GenreResponseDTO.class))).thenThrow(new RuntimeException());

		this.loadMoviesService.loadMovies();

		ArgumentCaptor<MovieDetailsResponseDTO> captor = ArgumentCaptor.forClass(MovieDetailsResponseDTO.class);
		Mockito.verify(saveMoviesService).saveMovieList(captor.capture());

		movieDetailsResponseDTO = captor.getValue();

		assertEquals(1, movieDetailsResponseDTO.getResults().size());
		assertEquals(0, movieDetailsResponseDTO.getResults().getFirst().getGenres().size());

	}

	private void MockEnvProperties() {
		Mockito.when(this.env.getProperty("api.movie.baseUrl", "")).thenReturn("baseUrl");
		Mockito.when(this.env.getProperty("api.movie.discover.path", "")).thenReturn("discover.path");
		Mockito.when(this.env.getProperty("api.movie.apiKey", "")).thenReturn("apiKey");
		Mockito.when(this.env.getProperty("api.movie.genre.path", "")).thenReturn("genre.path");
	}

	private MovieDetailsResponseDTO getMovieDetailsResponseDTO() {
		List<Integer> genreIds = new ArrayList<Integer>();
		genreIds.add(1);
		genreIds.add(2);

		MovieDetailsDTO movieDetailsDTO = new MovieDetailsDTO();
		movieDetailsDTO.setId(1L);
		movieDetailsDTO.setGenreIds(genreIds);

		List<MovieDetailsDTO> results = new ArrayList<MovieDetailsDTO>();
		results.add(movieDetailsDTO);

		MovieDetailsResponseDTO movieDetailsResponseDTO = new MovieDetailsResponseDTO();
		movieDetailsResponseDTO.setResults(results);

		return movieDetailsResponseDTO;
	}

	private GenreResponseDTO getGenreResponseDTO() {
		GenreDTO genreDTO1 = new GenreDTO();
		genreDTO1.setId(1);
		genreDTO1.setName("genre1");

		GenreDTO genreDTO2 = new GenreDTO();
		genreDTO2.setId(2);
		genreDTO2.setName("genre2");

		List<GenreDTO> genres = new ArrayList<GenreDTO>();
		genres.add(genreDTO1);
		genres.add(genreDTO2);

		GenreResponseDTO genreResponseDTO = new GenreResponseDTO();
		genreResponseDTO.setGenres(genres);

		return genreResponseDTO;
	}
}
