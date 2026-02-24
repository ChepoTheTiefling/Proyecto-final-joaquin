package com.example.demo.objects;

import java.util.List;

import jakarta.validation.constraints.*;

public class Autorizados {
	
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;
	
	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "Introduce un correo válido")
	private String correo;
	
	@NotBlank(message = "El teléfono es obligatorio")
	@Pattern(
			regexp = "^[0-9]{9}$",
			message = "El número debe tener 9 dígitos"
			)
	private String tlf;
	
	@NotBlank
	private String addres_cliente;
	
	private String address;
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getCorreo() {
		return correo;
	}
	
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	public String getTlf() {
		return tlf;
	}
	
	public void setTlf(String tlf) {
		this.tlf = tlf;
	}

	public String getAddres_cliente() {
		return addres_cliente;
	}

	public void setAddres_cliente(String addres_cliente) {
		this.addres_cliente = addres_cliente;
	}
}
