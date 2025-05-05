package gracia.marlon.playground.mvc.mapper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import gracia.marlon.playground.mvc.dtos.MovieDetailsDTO;
import gracia.marlon.playground.mvc.model.Movie;

public class MovieDetailsMapperTest {

	@Test
	public void toEntitySuccessful() {
		Movie movie = MovieDetailsMapper.INSTANCE.toEntity(null);
		assertEquals(null, movie);

		MovieDetailsDTO movieDetailsDTO = new MovieDetailsDTO();
		movie = MovieDetailsMapper.INSTANCE.toEntity(movieDetailsDTO);
		assertEquals(0, movie.getId().intValue());
		assertEquals(null, movie.getTitle());

		List<Integer> genreIdsList = new ArrayList<Integer>();
		List<String> genresList = new ArrayList<String>();

		movieDetailsDTO = new MovieDetailsDTO();
		movieDetailsDTO.setCreateOn(new Date());
		movieDetailsDTO.setGenreIds(genreIdsList);
		movieDetailsDTO.setGenres(genresList);
		movieDetailsDTO.setId(1L);
		movieDetailsDTO.setOriginalLanguage("originalLanguage");
		movieDetailsDTO.setOriginalTitle("originalTitle");
		movieDetailsDTO.setOverview("overview");
		movieDetailsDTO.setPopularity(0.0);
		movieDetailsDTO.setPosterPath("posterPath");
		movieDetailsDTO.setReleaseDate("releaseDate");
		movieDetailsDTO.setTitle("tittle");
		movieDetailsDTO.setVoteAverage(0.0);
		movieDetailsDTO.setVoteCount(0.0);

		movie = MovieDetailsMapper.INSTANCE.toEntity(movieDetailsDTO);
		assertEquals(1L, movie.getId().longValue());
		assertEquals("tittle", movie.getTitle());

	}
}
