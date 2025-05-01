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
import com.example.demo.dto.RoleDTO;
import com.example.demo.service.RoleService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController 
@Tag(name="Role Controller")
public class RoleAPI {
	
	@Autowired
	private RoleService roleservice;
	
	@PostMapping(value="role/crate")
	public ApiResponse<RoleDTO> create(@RequestBody RoleDTO dto){
		ApiResponse<RoleDTO> apires = new ApiResponse<RoleDTO>();
		apires.setCode(200);
		apires.setMessage("create successfull1!");
		apires.setResult(roleservice.createRole(dto));
		return apires;
		
	}
	
	@GetMapping(value = "role/getall")
//	public ApiResponse<List<RoleDTO>> getall(){
//		List<RoleDTO> listPermis = roleservice.getAllRole();
//		
//		ApiResponse<List<RoleDTO>> apires = new ApiResponse<List<RoleDTO>>();
//		apires.setCode(200);
//		apires.setMessage("Successfull");
//		apires.setResult(listPermis);
//		
//		return apires;
//	}
	
	@DeleteMapping(value = "role/delete/{name}")
	public ApiResponse<String> delete(@PathVariable String name){
		
		ApiResponse<String> apires = new ApiResponse<String>();
		
		apires.setCode(200);
		apires.setMessage("Delete Successful");
		roleservice.deleteRole(name);
		return apires;
	}
}
