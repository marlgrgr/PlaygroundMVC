package gracia.marlon.playground.mvc.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import gracia.marlon.playground.mvc.dtos.RoleDTO;
import gracia.marlon.playground.mvc.mapper.RoleMapper;
import gracia.marlon.playground.mvc.model.Role;
import gracia.marlon.playground.mvc.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

	private final RoleRepository roleRepository;

	private final RoleMapper roleMapper;

	@Override
	@Cacheable(cacheNames = "RoleService_getRoles", key = "'getRoles'", unless = "#result == null")
	public List<RoleDTO> getRoles() {
		List<Role> roleList = this.roleRepository.findAll();

		List<RoleDTO> roleDTOList = roleList.stream().map(this.roleMapper::toDto).collect(Collectors.toList());

		return roleDTOList;
	}

}
