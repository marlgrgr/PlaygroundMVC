package gracia.marlon.playground.mvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gracia.marlon.playground.mvc.dtos.UserRoleDTO;
import gracia.marlon.playground.mvc.model.UserRole;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

	UserRoleMapper INSTANCE = Mappers.getMapper(UserRoleMapper.class);

	UserRoleDTO toDto(UserRole entity);

}
