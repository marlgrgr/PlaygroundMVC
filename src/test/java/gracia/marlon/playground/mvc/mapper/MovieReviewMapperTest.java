package gracia.marlon.playground.mvc.mapper;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.model.MovieReview;

public class MovieReviewMapperTest {

	@Test
	public void toDtoSuccessful() {
		MovieReviewDTO movieReviewDTO = MovieReviewMapper.INSTANCE.toDto(null);
		assertEquals(null, movieReviewDTO);

		MovieReview movieReview = new MovieReview();
		movieReview.setCreateOn(new Date());
		movieReview.setId("id");
		movieReview.setMovieId(1L);
		movieReview.setReview("review");
		movieReview.setScore(0.0);

		movieReviewDTO = MovieReviewMapper.INSTANCE.toDto(movieReview);
		assertEquals("id", movieReviewDTO.getId());
		assertEquals("review", movieReviewDTO.getReview());
	}

	@Test
	public void toEntitySuccessful() {
		MovieReview movieReview = MovieReviewMapper.INSTANCE.toEntity(null);
		assertEquals(null, movieReview);

		MovieReviewDTO movieReviewDTO = new MovieReviewDTO();
		movieReviewDTO.setCreateOn(new Date());
		movieReviewDTO.setId("id");
		movieReviewDTO.setMovieId(1L);
		movieReviewDTO.setReview("review");
		movieReviewDTO.setScore(0.0);

		movieReview = MovieReviewMapper.INSTANCE.toEntity(movieReviewDTO);
		assertEquals("id", movieReview.getId());
		assertEquals("review", movieReview.getReview());
	}

}
