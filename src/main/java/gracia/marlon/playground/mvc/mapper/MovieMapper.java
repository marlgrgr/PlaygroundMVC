package gracia.marlon.playground.mvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gracia.marlon.playground.mvc.dtos.MovieDTO;
import gracia.marlon.playground.mvc.model.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {

	MovieMapper INSTANCE = Mappers.getMapper(MovieMapper.class);

	MovieDTO toDto(Movie entity);

}
