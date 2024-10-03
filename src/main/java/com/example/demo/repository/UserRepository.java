package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
	UserEntity findOneById(String id);

	boolean existsById(String id);

	boolean existsByUsername(String username);

	List<UserEntity> findOneByUsername(String username);

//	update 24/9
	List<UserEntity> findAll();

//update 26/9
	List<UserEntity> findByUsername(String username);
}
