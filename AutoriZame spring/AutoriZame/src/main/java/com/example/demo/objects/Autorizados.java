package com.example.demo.objects;

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

	@NotBlank(message = "La identificación es obligatoria")
	private String identificacion;
	
	private String addres_cliente;
	
	@NotBlank(message = "La dirección es obligatoria")
	@Pattern(
			regexp = "^0x[a-fA-F0-9]{40}$",
			message = "La dirección debe ser una dirección válida de Ethereum"
			)
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

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getAddres_cliente() {
		return addres_cliente;
	}

	public void setAddres_cliente(String addres_cliente) {
		this.addres_cliente = addres_cliente;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
