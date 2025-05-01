package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.apiresponse.ApiResponse;
import com.example.demo.dto.PermissionDTO;
import com.example.demo.service.PermissionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name="Permission Controller")
public class PermissionAPI {

		@Autowired
		private PermissionService permissionservice;
		
		@PostMapping(value="permisson/crate")
		public ApiResponse<PermissionDTO> create(@RequestBody PermissionDTO dto){
			ApiResponse<PermissionDTO> apires = new ApiResponse<PermissionDTO>();
			apires.setCode(200);
			apires.setMessage("create successfull1!");
			apires.setResult(permissionservice.createPermission(dto));
			return apires;
			
		}
		
		@GetMapping(value = "permission/getall")
		public ApiResponse<List<PermissionDTO>> getall(){
			List<PermissionDTO> listPermis = permissionservice.getAllPermission();
			
			ApiResponse<List<PermissionDTO>> apires = new ApiResponse<List<PermissionDTO>>();
			apires.setCode(200);
			apires.setMessage("Successfull");
			apires.setResult(listPermis);
			
			return apires;
		}
		
		@DeleteMapping(value = "permission/delete/{name}")
		public ApiResponse<String> deletePermis(@PathVariable String name){
			
			ApiResponse<String> apires = new ApiResponse<String>();
			
			apires.setCode(200);
			apires.setMessage("Delete Successful");
			permissionservice.deletePermision(name);
			return apires;
		}
	
}
