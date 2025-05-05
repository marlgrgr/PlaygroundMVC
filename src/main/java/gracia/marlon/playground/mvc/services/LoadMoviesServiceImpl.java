package gracia.marlon.playground.mvc.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import gracia.marlon.playground.mvc.dtos.GenreResponseDTO;
import gracia.marlon.playground.mvc.dtos.MovieDetailsResponseDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoadMoviesServiceImpl implements LoadMoviesService {

	private final RestTemplate restTemplate;

	private final SaveMoviesService saveMoviesService;

	private final String apiMovieBaseUrl;

	private final String apiMovieDiscoverPath;

	private final String apiMovieApiKey;

	private final String apiMovieGenrePath;

	private ConcurrentMap<Integer, String> genreMap = new ConcurrentHashMap<Integer, String>();

	public LoadMoviesServiceImpl(RestTemplate restTemplate, SaveMoviesService saveMoviesService, Environment env) {
		this.restTemplate = restTemplate;
		this.saveMoviesService = saveMoviesService;
		this.apiMovieBaseUrl = env.getProperty("api.movie.baseUrl", "");
		this.apiMovieDiscoverPath = env.getProperty("api.movie.discover.path", "");
		this.apiMovieGenrePath = env.getProperty("api.movie.genre.path", "");
		this.apiMovieApiKey = env.getProperty("api.movie.apiKey", "");
	}

	@Override
	public void loadMovies() {

		if (!validURLParameters()) {
			log.info("Movies can not be load, not all parameters are configured");
			return;
		}

		final MovieDetailsResponseDTO movieDetailsResponseDTO = getMoviesFromService();
		this.saveMoviesService.saveMovieList(movieDetailsResponseDTO);
	}

	private MovieDetailsResponseDTO getMoviesFromService() {
		MovieDetailsResponseDTO movieDetailsResponseDTO = null;
		try {

			String urlDiscoverMovies = this.apiMovieBaseUrl + this.apiMovieDiscoverPath;

			final HttpEntity<Void> requestEntity = this.getEntityWithAuth();

			urlDiscoverMovies = this.getMovieDiscoverUrl(urlDiscoverMovies);

			final ResponseEntity<MovieDetailsResponseDTO> movieDetailsResponse = restTemplate
					.exchange(urlDiscoverMovies, HttpMethod.GET, requestEntity, MovieDetailsResponseDTO.class);

			if (movieDetailsResponse == null  || movieDetailsResponse.getStatusCode() == null || !movieDetailsResponse.getStatusCode().is2xxSuccessful()
					|| movieDetailsResponse.getBody() == null) {
				String statusCodeDetails = movieDetailsResponse != null && movieDetailsResponse.getStatusCode() != null
						? " Status code: " + movieDetailsResponse.getStatusCode().value()
						: "";
				log.error("An error occurred while retrieving the movie listing." + statusCodeDetails);
				return null;
			}

			movieDetailsResponseDTO = movieDetailsResponse.getBody();

			if (!this.validMovieDetailsResponseDTO(movieDetailsResponseDTO)) {
				return movieDetailsResponseDTO;
			}

			final List<Integer> genreList = movieDetailsResponseDTO.getResults().stream().filter(result -> result.getGenreIds() != null)
					.flatMap(result -> result.getGenreIds().stream()).distinct().collect(Collectors.toList());
			this.callLoadGenres(genreList);

			movieDetailsResponseDTO.getResults().stream()
					.forEach(result -> result.setGenres(this.getGenreList(result.getGenreIds())));
		} catch (Exception e) {
			log.error("An error occurred while retrieving the movie list from the external service", e);
			return null;
		}

		return movieDetailsResponseDTO;
	}

	private void callLoadGenres(List<Integer> genre) {
		if (genre.isEmpty()) {
			return;
		}

		final boolean thereAreGenresMissing = genre.stream().anyMatch(x -> !genreMap.containsKey(x));

		if (!thereAreGenresMissing) {
			return;
		}

		try {

			final String urlGenreMovies = this.apiMovieBaseUrl + this.apiMovieGenrePath;

			final HttpEntity<Void> requestEntity = this.getEntityWithAuth();

			final ResponseEntity<GenreResponseDTO> genreResponse = restTemplate.exchange(urlGenreMovies, HttpMethod.GET,
					requestEntity, GenreResponseDTO.class);

			if (genreResponse == null || genreResponse.getStatusCode() == null || !genreResponse.getStatusCode().is2xxSuccessful()
					|| genreResponse.getBody() == null) {
				String statusCodeDetails = genreResponse != null && genreResponse.getStatusCode() != null
						? " Status code: " + genreResponse.getStatusCode().value()
						: "";
				log.error("An error occurred while retrieving the genres listing." + statusCodeDetails);
				return;
			}

			final GenreResponseDTO genreResponseDTO = genreResponse.getBody();

			this.genreMap.clear();

			if (genreResponseDTO.getGenres() == null || genreResponseDTO.getGenres().isEmpty()) {
				return;
			}

			genreResponseDTO.getGenres().stream()
					.forEach(element -> this.genreMap.put(element.getId(), element.getName()));

		} catch (Exception e) {
			log.error("An error occurred while retrieving the genres listing.", e);
		}
	}

	private String getMovieDiscoverUrl(String baseUrl) {

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		final LocalDate today = LocalDate.now();
		final String todayFormatted = today.format(formatter);

		final LocalDate aMonthAgo = today.minusMonths(1);
		final String aMonthAgoFormatted = aMonthAgo.format(formatter);

		final String finalUrl = baseUrl + "&release_date.gte=" + aMonthAgoFormatted + "&release_date.lte="
				+ todayFormatted;

		return finalUrl;
	}

	private List<String> getGenreList(List<Integer> genreIds) {
		if (genreIds == null || genreIds.isEmpty()) {
			return new ArrayList<String>();
		}

		return genreIds.stream().map(x -> this.genreMap.get(x)).filter(x -> x != null).collect(Collectors.toList());
	}

	private boolean validURLParameters() {
		return !this.apiMovieBaseUrl.isEmpty() && !this.apiMovieDiscoverPath.isEmpty() && !this.apiMovieApiKey.isEmpty()
				&& !this.apiMovieGenrePath.isEmpty();
	}

	private HttpEntity<Void> getEntityWithAuth() {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + this.apiMovieApiKey);
		headers.add("content-type", "application/json");

		final HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		return requestEntity;
	}

	private boolean validMovieDetailsResponseDTO(MovieDetailsResponseDTO movieDetailsResponseDTO) {
		return movieDetailsResponseDTO.getResults() != null
				&& !movieDetailsResponseDTO.getResults().isEmpty();
	}
}
