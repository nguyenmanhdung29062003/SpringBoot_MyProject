package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.PermissionDTO;
import com.example.demo.entity.PermissionEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.repository.PermisionRepository;

@Service
public class PermissionService {
	@Autowired
	private PermissionMapper permissionmapper;
	
	@Autowired
	private PermisionRepository permissionrepository;
	
	
	//create an permission
	public PermissionDTO createPermission(PermissionDTO dto) {
		if (permissionrepository.existsByName(dto.getName())) {
			throw new AppException(ErrorCode.PERMISSION_EXIST);
		}
		
		PermissionEntity entity = permissionmapper.toEntity(dto);
		entity = permissionrepository.save(entity);
		
		return permissionmapper.toDTO(entity);
		
	}
	
	//get List Permission
	public List<PermissionDTO> getAllPermission(){
		
		List<PermissionEntity> listEntity = permissionrepository.findAll();
		return listEntity.stream().map(permissionmapper::toDTO).toList();
		
	}
	
	//delete a permission
	public void deletePermision(String name)
	{
		if (!permissionrepository.existsByName(name)) {
			throw new AppException(ErrorCode.PERMISSION_ERROR);
		}
		permissionrepository.deleteById(name);
	}
}
