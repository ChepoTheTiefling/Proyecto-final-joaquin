package com.example.demo.objects;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Entity
@Table(name = "administradores", uniqueConstraints = {
		@UniqueConstraint(columnNames = "address")
})
public class Administradores {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String address;
	
	@NotBlank
	@Column(nullable = false)
	private String password;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
}
