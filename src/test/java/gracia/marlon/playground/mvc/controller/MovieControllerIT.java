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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.repository.MovieRepository;

public class MovieControllerIT extends AbstractIntegrationBase {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MovieRepository movieRepository;

	@BeforeAll
	private void setUpMongoRepository() {
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
	}

	@Test
	void movieIT() throws Exception {
		mockMvc.perform(post("/api/v1/movie/load").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isAccepted());

		MvcResult response = mockMvc.perform(get("/api/v1/movie").header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		PagedResponse<MovieDTO> pagedResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<PagedResponse<MovieDTO>>() {
				});

		Long idFirstResult = pagedResponse.getResults().getFirst().getId();

		assertEquals(1L, pagedResponse.getTotalResults());
		assertEquals("tittle", pagedResponse.getResults().getFirst().getTitle());

		response = mockMvc
				.perform(get("/api/v1/movie/" + idFirstResult).header("Authorization", "Bearer " + this.getToken()))
				.andExpect(status().isOk()).andReturn();

		MovieDTO movieDTO = objectMapper.readValue(response.getResponse().getContentAsString(), MovieDTO.class);
		assertEquals("tittle", movieDTO.getTitle());
	}

}
