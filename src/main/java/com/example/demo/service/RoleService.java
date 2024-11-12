package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.demo.dto.RoleDTO;
import com.example.demo.entity.RoleEntity;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.repository.PermisionRepository;
import com.example.demo.repository.RolerRepository;

@Service
public class RoleService {
	@Autowired
	private RoleMapper rolemapper;
	
	@Autowired
	private RolerRepository rolerepository;
	@Autowired
	private PermisionRepository permisionRepository;
	
	//create an permission
		public RoleDTO createRole(RoleDTO dto) {
			if (rolerepository.existsByName(dto.getName())) {
				throw new AppException(ErrorCode.PERMISSION_EXIST);
			}
			
			RoleEntity entity = rolemapper.toEntity(dto,permisionRepository);
			entity = rolerepository.save(entity);
			
			return rolemapper.toDTO(entity,permisionRepository);
			
		}
		
		//get List Permission
//		public List<RoleDTO> getAllRole(){
//			
//			List<RoleEntity> listEntity = rolerepository.findAll();
//			return listEntity.stream().map(rolemapper::toDTO).toList();
//			
//		}
		
		//delete a permission
		public void deleteRole(String name)
		{
			if (!rolerepository.existsByName(name)) {
				throw new AppException(ErrorCode.PERMISSION_ERROR);
			}
			rolerepository.deleteById(name);
		}
}
