package gracia.marlon.playground.mvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gracia.marlon.playground.mvc.dtos.MovieDetailsDTO;
import gracia.marlon.playground.mvc.model.Movie;

@Mapper(componentModel = "spring")
public interface MovieDetailsMapper {

	MovieDetailsMapper INSTANCE = Mappers.getMapper(MovieDetailsMapper.class);

	Movie toEntity(MovieDetailsDTO dto);

}
