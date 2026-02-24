package com.example.demo.objects;

import java.util.List;

import jakarta.validation.constraints.*;

public class Repartidores {
	
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;
	
	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "Introduce un correo válido")
	private String correo;
	
	@Pattern(
			regexp = "^[0-9]{9}$",
			message = "El número debe tener 9 dígitos"
			)
	private String tlf;
	
	@NotBlank(message = "La dirección es obligatoria")
    @Pattern(
        regexp = "^0x[a-fA-F0-9]{40}$",
        message = "La dirección debe ser una dirección válida de Ethereum"
    )
	private String address_empresa;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$",
			message = "La contraseña debe tener al menos una mayúscula y un símbolo"
			)
	private String password;
	
	private List<String> notificaciones;
	
	public enum Estado{
		Activo,
		Inactivo
	}
	
	private Estado estado = Estado.Activo;
	
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

	public String getAddress_empresa() {
		return address_empresa;
	}

	public void setAddress_empresa(String address_empresa) {
		this.address_empresa = address_empresa;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Estado getEstado() {
		return estado;
	}
	
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<String> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<String> notificaciones) {
		this.notificaciones = notificaciones;
	}
}
