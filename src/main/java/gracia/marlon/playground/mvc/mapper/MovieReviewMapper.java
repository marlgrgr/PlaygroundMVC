package gracia.marlon.playground.mvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gracia.marlon.playground.mvc.dtos.MovieReviewDTO;
import gracia.marlon.playground.mvc.model.MovieReview;

@Mapper(componentModel = "spring")
public interface MovieReviewMapper {

	MovieReviewMapper INSTANCE = Mappers.getMapper(MovieReviewMapper.class);

	MovieReviewDTO toDto(MovieReview entity);

	MovieReview toEntity(MovieReviewDTO dto);
}
