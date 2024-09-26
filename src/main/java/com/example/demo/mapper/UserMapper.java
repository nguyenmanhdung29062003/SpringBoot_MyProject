package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserEntity toUser(UserDTO dto);
	UserDTO toDTO(UserEntity entity);
//	@mappingtarget giúp map data từ DTO đc gửi từ client sang ENTITY để câp nhật vào database
	void updateUser(@MappingTarget UserEntity userentty, UserDTO userdto);
}
