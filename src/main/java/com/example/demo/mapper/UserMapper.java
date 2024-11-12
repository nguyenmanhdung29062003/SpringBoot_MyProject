package com.example.demo.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.PermisionRepository;
import com.example.demo.repository.RolerRepository;
import com.example.demo.repository.UserRepository;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	@Mapping(target = "roles", source = "roles")
	UserEntity toUser(UserDTO dto,@Context RolerRepository roleRepository);
	
	@Mapping(target = "roles", source = "roles")
	UserDTO toDTO(UserEntity entity,@Context RolerRepository roleRepository);
//	@mappingtarget giúp map data từ DTO đc gửi từ client sang ENTITY để câp nhật vào database
	@Mapping(target = "roles", source = "roles")
	void updateUser(@MappingTarget UserEntity userentty, UserDTO userdto,@Context RolerRepository roleRepository);
	
	 default Set<RoleEntity> mapRoles(Set<String> rolesNames, @Context RolerRepository roleRepository) {
	        Set<RoleEntity> roleEntities = new HashSet<>();
	        for (String name : rolesNames) {
	            	RoleEntity role = roleRepository.findByName(name);
	            if (role == null) {
	                throw new AppException(ErrorCode.PERMISSION_ERROR);
	            }
	            roleEntities.add(role);
	        }
	        return roleEntities;
	    }

	    default Set<String> mapRolestoName(Set<RoleEntity> roleEntities) {
	        Set<String> roleNames = new HashSet<>();
	        for (RoleEntity role : roleEntities) {
	            String name =role.getName();
	            if (name == null) {
	                throw new AppException(ErrorCode.PERMISSION_ERROR);
	            }
	            roleNames.add(name);
	        }
	        return roleNames;
	    }
}
