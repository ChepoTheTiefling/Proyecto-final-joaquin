package com.example.demo.objects;

import jakarta.validation.constraints.*;

public class Administradores {
	
	@NotBlank
	private String address;
	
	@NotBlank
	private String password;
	
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
