package com.example.demo.objects;

import jakarta.validation.constraints.*;

public class Empresas {
	
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;
	
	@NotBlank(message = "El correo es obligatorio")
	@Email
	private String mail;
	
	@NotBlank(message = "La dirección es obligatoria")
	@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección debe ser una dirección válida de Ethereum"
	    )
	private String address;
	
	@NotBlank(message = "El teléfono es obligatorio")
	@Pattern(regexp = "^[0-9]{6,15}$", message = "Número de teléfono inválido")
	private String tlf;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$",
			message = "La contraseña debe tener al menos una mayúscula y un símbolo"
			)
	private String password;
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getTlf() {
		return tlf;
	}

	public void setTlf(String tlf) {
		this.tlf = tlf;
	}

	public void setMail(String mail) {
		this.mail = mail;
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
