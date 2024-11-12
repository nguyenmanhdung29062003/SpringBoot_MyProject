package com.example.demo.entity;

import java.time.LocalDate;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblUser")
public class UserEntity {
	
	@Id
	@Column(name = "ID")
	private String id;
	
	@Column(name = "Username")
	private String username;
	
	@Column(name = "Password")
	@Size(min=8,message="PASS_ERROR")
	private String password;
	
	@Column(name = "FirstName")
	private String firstName;
	
	@Column(name = "LastName")
	private String lastName;
	
	@Column(name = "DateofBirth")
	private LocalDate dob;
	
	@ManyToMany
	private Set<RoleEntity> roles;
}