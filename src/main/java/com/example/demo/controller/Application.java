package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.apiresponse.ApiResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class Application {

	@Autowired
	private UserService userservice;

	@PostMapping("/user")
	public ApiResponse<UserDTO> addUser(@RequestBody @Valid UserDTO usea) {
		ApiResponse<UserDTO> apires = new ApiResponse<UserDTO>();
		apires.setCode(202);
		apires.setMessage("success");
		apires.setResult(userservice.save(usea));

		return apires;
	}
//	update 24/92024
	@GetMapping(value="/users")
	public ApiResponse<List<UserDTO>> getUsers(){
//		get infor đăng nhập, đang đc authicate hiện tại
		var authenticated = SecurityContextHolder.getContext().getAuthentication();
//		bây giờ lấy thông tin trong TOKEN và log ra
		log.info("USER NAME : "+authenticated.getName());
		
		Jwt jwt = (Jwt) authenticated.getPrincipal();
		String roles = jwt.getClaimAsString("roles");
		log.info("TOKEN ROLE  :"+roles);
		
		
		authenticated.getAuthorities().forEach(gra -> log.info(gra.getAuthority()));
		
		
		ApiResponse<List<UserDTO>> apires = new ApiResponse<List<UserDTO>>();	
		apires.setCode(200);
		apires.setMessage("success");
		apires.setResult(userservice.getallusers());
		
		return apires;
		
	}
	
	//6/10/2024
	@GetMapping(value="/user/myinfo")
	public ApiResponse<UserDTO> getMyinfo(){
		ApiResponse<UserDTO> apires = new ApiResponse<UserDTO>();	
		apires.setCode(200);
		apires.setMessage("success");
		apires.setResult(userservice.getMyInfo());
		
		return apires;
		
	}

	@GetMapping(value = "/user/{id}")
	public ApiResponse<UserDTO> getUsername(@PathVariable("id") String id) {
		ApiResponse<UserDTO> apires = new ApiResponse<UserDTO>();
		apires.setCode(200);
		apires.setMessage("success");
		apires.setResult(userservice.find(id));
		return apires;
	}
	
	@PutMapping(value = "/user/{id}")
	public ApiResponse<UserDTO> upDateInf(@PathVariable("id") String id,@RequestBody @Valid UserDTO dto){
		ApiResponse<UserDTO> apires = new ApiResponse<UserDTO>();
		apires.setCode(200);
		apires.setMessage("success");
		apires.setResult(userservice.update(id, dto));
		return apires;

	}
}
