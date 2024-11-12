package com.example.demo.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblInvalidateTOKEN")
public class InvalidatedToken {
	@Id
	@Column(name="ID")
	private String id;
	
	@Column(name="expTime")
	private Date expiryTime;
	
	
}
