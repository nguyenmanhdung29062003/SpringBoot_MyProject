package com.example.demo.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PermissionEntity;
import com.example.demo.entity.RoleEntity;
@Repository
public interface RolerRepository extends JpaRepository<RoleEntity, String>{
	boolean existsByName(String name);
	//EntityGraph cho phép bạn tải các quan hệ lazy một cách có chọn lọc mà không cần thay đổi FetchType thành EAGER. 
	//Điều này giúp bạn có thể tối ưu hiệu suất bằng cách chỉ tải dữ liệu cần thiết cho mỗi use case cụ thể.
	
	@EntityGraph(attributePaths = {"permissions"})
	RoleEntity  findByName(String name);
}
