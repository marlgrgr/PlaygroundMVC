package gracia.marlon.playground.mvc.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.model.Movie;

public class MovieMapperTest {

	@Test
	public void toDtoSuccessful() {
		MovieDTO movieDTO = MovieMapper.INSTANCE.toDto(null);
		assertEquals(null, movieDTO);

		Movie movie = new Movie();
		movieDTO = MovieMapper.INSTANCE.toDto(movie);
		assertEquals(0, movieDTO.getId());
		assertEquals(null, movieDTO.getTitle());

		List<Integer> genreIdsList = new ArrayList<Integer>();
		List<String> genresList = new ArrayList<String>();

		movie = new Movie();
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

		movieDTO = MovieMapper.INSTANCE.toDto(movie);
		assertEquals(1L, movieDTO.getId());
		assertEquals("tittle", movieDTO.getTitle());

	}
}
