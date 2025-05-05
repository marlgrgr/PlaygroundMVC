package gracia.marlon.playground.mvc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

	RoleDTO toDto(Role entity);
}
