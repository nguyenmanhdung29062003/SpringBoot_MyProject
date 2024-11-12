package com.example.demo.entity;

import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblRole")
public class RoleEntity {
	
	@Id
	@Column(name = "Name")
	private String name;
	@Column(name ="Description")
	private String description;
	
	//tạo bảng role_permission quan hệ nhiều nhiều giữa Role và Permission
	@ManyToMany
	private Set<PermissionEntity> permissions;
}
