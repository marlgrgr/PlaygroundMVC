package gracia.marlon.playground.mvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gracia.marlon.playground.mvc.dtos.UserDTO;
import gracia.marlon.playground.mvc.model.Users;

@Mapper(componentModel = "spring")
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	UserDTO toDto(Users entity);

}
