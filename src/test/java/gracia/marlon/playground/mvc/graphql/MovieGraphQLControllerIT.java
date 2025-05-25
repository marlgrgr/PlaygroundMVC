package gracia.marlon.playground.mvc.graphql;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import gracia.marlon.playground.mvc.configuration.AbstractIntegrationBase;
import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.dtos.PagedResponse;
import gracia.marlon.playground.mvc.model.Movie;
import gracia.marlon.playground.mvc.repository.MovieRepository;

public class MovieGraphQLControllerIT extends AbstractIntegrationBase {

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

	@SuppressWarnings("unchecked")
	@Test
	void movieIT() throws Exception {
		String mutation = """
				mutation LoadMovies {
				    loadMovies
				}
				""";

		String query = "{\"query\": \"" + mutation.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		MvcResult response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		Map<String, Object> responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> responseData = (Map<String, Object>) responseMap.get("data");
		Boolean responseBoolean = (Boolean) responseData.get("loadMovies");
		assertEquals(true, responseBoolean);

		String queryStr = """
				query GetMovieDetailsResponseDTO {
				    getMovieDetailsResponseDTO {
				        page
				        totalPages
				        totalResults
				        results {
				            id
				            title
				        }
				    }
				}
				""";

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");
		PagedResponse<MovieDTO> pagedResponse = objectMapper.convertValue(
				responseData.get("getMovieDetailsResponseDTO"), new TypeReference<PagedResponse<MovieDTO>>() {
				});

		Long idFirstResult = pagedResponse.getResults().getFirst().getId();

		assertEquals(1L, pagedResponse.getTotalResults());
		assertEquals("tittle", pagedResponse.getResults().getFirst().getTitle());

		queryStr = String.format("""
				query GetMovieById {
				    getMovieById(movieId: \"%s\" ) {
				        id
				        title
				    }
				}
				""", idFirstResult);

		query = "{\"query\": \"" + queryStr.replace("\"", "\\\"").replace("\t", " ").replace("\n", "") + "\"}";

		response = mockMvc.perform(post("/graphql").header("Authorization", "Bearer " + this.getToken())
				.contentType(MediaType.APPLICATION_JSON).content(query)).andExpect(status().isOk()).andReturn();

		responseMap = objectMapper.readValue(response.getResponse().getContentAsString(),
				new TypeReference<Map<String, Object>>() {
				});

		responseData = (Map<String, Object>) responseMap.get("data");
		MovieDTO movieDTO = objectMapper.convertValue(responseData.get("getMovieById"), MovieDTO.class);
		assertEquals("tittle", movieDTO.getTitle());
	}

}
