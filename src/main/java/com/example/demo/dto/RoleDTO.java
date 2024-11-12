package com.example.demo.dto;

import java.util.Set;

import com.example.demo.entity.PermissionEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
	
	private String name;
	private String description;
	private Set<String> permissions;
}
