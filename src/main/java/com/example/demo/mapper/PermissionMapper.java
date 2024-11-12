package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.demo.dto.PermissionDTO;

import com.example.demo.entity.PermissionEntity;
import com.example.demo.repository.PermisionRepository;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
	PermissionEntity toEntity(PermissionDTO dto);

	PermissionDTO toDTO(PermissionEntity entity);

//	@mappingtarget giúp map data từ DTO đc gửi từ client sang ENTITY để câp nhật vào database
	void updateUser(@MappingTarget PermissionEntity userentty, PermissionDTO userdto);
	
	
	 // Giả sử đây là hàm tìm PermissionEntity từ name
//    default PermissionEntity findPermissionByName(String name) {
//        // Triển khai logic tìm kiếm PermissionEntity dựa trên name từ database hoặc từ một service nào đó
//    	PermisionRepository permisionreposi
//    	
//    	
//        return new PermisionEntity(name); // Đây chỉ là ví dụ, bạn cần thay thế bằng logic thực tế
//    }
}
