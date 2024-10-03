package com.example.demo.dto;

import java.util.Set;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String id;
	private String username;
	@Size(min = 8, message = "PASS_ERROR")
	private String password;
	private Set<String> roles;
}
