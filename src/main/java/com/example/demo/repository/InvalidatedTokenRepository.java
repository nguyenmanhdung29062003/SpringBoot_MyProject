package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.InvalidatedToken;


@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String>{
	boolean existsById(String id);
	
}