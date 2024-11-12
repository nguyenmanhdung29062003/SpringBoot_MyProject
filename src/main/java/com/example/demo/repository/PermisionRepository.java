package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.PermissionEntity;
@Repository
public interface PermisionRepository extends JpaRepository<PermissionEntity, String>{
	boolean existsByName(String name);
	PermissionEntity  findByName(String name);
}
