package gracia.marlon.playground.mvc.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.model.MovieReview;
import gracia.marlon.playground.mvc.repository.MovieRepository;
import gracia.marlon.playground.mvc.repository.MovieReviewRepository;
import gracia.marlon.playground.mvc.services.CacheService;

public class MovieReviewControllerIT extends AbstractIntegrationBase {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private MovieReviewRepository movieReviewRepository;

	@Autowired
	private CacheService cacheService;

	@BeforeAll
	private void setUpMongoRepository() {
		this.movieReviewRepository.deleteAll();
		this.movieRepository.deleteAll();

		List<Integer> genreIdsList = new ArrayList<Integer>();
		List<String> genresList = new ArrayList<String>();

		Movie movie = new Movie();
		movie.setCreateOn(new Date());
		movie.setGenreIds(genreIdsList);
		movie.setGenres(genresList);
		movie.setId(1L);
		movie.setOriginalLanguage("originalLanguage");
		movie.setOriginalTitle("originalTitle");
		movie.setOverview("overview");
		movie.setPopularity(0.0);
		movie.setPosterPath("posterPath");
		movie.setReleaseDate("releaseDate");
		movie.setTitle("tittle");
		movie.setVoteAverage(0.0);
		movie.setVoteCount(0.0);

		this.movieRepository.save(movie);

		this.cacheService.evictCache("MovieReviewService_getMovieReviews");
		this.cacheService.evictCache("MovieReviewService_getMovieReviewsByMovie");
		this.cacheService.evictCache("MovieReviewService_getMovieReviewByID");
		this.cacheService.evictCache("Flux_MovieReviewService_getMovieReviews");
		this.cacheService.evictCache("Flux_MovieReviewService_getMovieReviewsByMovie");
		this.cacheService.evictCache("Flux_MovieReviewService_getMovieReviewByID");
	}

	@Test
	void movieReviewIT() throws Exception {
		MvcResult response = mockMvc
				.perform(get("/api/v1/movieReview").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		PagedResponse<MovieReviewDTO> pagedResponse = objectMapper.readValue(
				response.getResponse().getContentAsString(), new TypeReference<PagedResponse<MovieReviewDTO>>() {
				});

		assertEquals(0, pagedResponse.getTotalResults());

		MovieReview movieReview = new MovieReview();
		movieReview.setCreateOn(new Date());
		movieReview.setId("id");
		movieReview.setMovieId(1L);
		movieReview.setReview("review");
		movieReview.setScore(0.0);

		mockMvc.perform(post("/api/v1/movieReview").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(movieReview)))
				.andExpect(status().isCreated());

		response = mockMvc.perform(get("/api/v1/movieReview").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<MovieReviewDTO>>() {
				});

		assertEquals(1, pagedResponse.getTotalResults());

		response = mockMvc.perform(get("/api/v1/movieReview/id").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		MovieReviewDTO movieReviewDTOResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				MovieReviewDTO.class);

		assertEquals("review", movieReviewDTOResponse.getReview());

		response = mockMvc
				.perform(get("/api/v1/movieReview/movie/1").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<MovieReviewDTO>>() {
				});

		assertEquals(1, pagedResponse.getTotalResults());
		assertEquals("review", pagedResponse.getResults().getFirst().getReview());
	}
}
