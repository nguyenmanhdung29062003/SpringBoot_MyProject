package com.example.demo.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.Role;
import com.example.demo.repository.PermisionRepository;
import com.example.demo.repository.RolerRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
	
	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository, RolerRepository roleRepository) {
		return args -> {
			if(userRepository.findByUsername("admin").isEmpty()) {
				//tim kiem
				
				//tạo Role
				RoleEntity role = roleRepository.findByName("ADMIN");
				log.warn(role.getPermissions().toString());
				
				
				Set<RoleEntity> roles = new HashSet<RoleEntity>();
				roles.add(role);
				
				//tạo User
				UserEntity et =  new UserEntity();
				et.setId("admin");
				et.setUsername("admin");
				PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
				et.setPassword(passwordEncoder.encode("admin123"));
				et.setRoles(roles);
				userRepository.save(et);
				
				log.warn("default admin user has been created with default password admin123, please change it");
				
			}
		};
	}
	 

}
