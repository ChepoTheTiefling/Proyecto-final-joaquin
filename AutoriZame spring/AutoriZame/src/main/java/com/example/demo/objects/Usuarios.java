package com.example.demo.objects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.demo.objects.Pedidos;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.*;

public class Usuarios {
	//Datos manuales
	
	@NotBlank(message = "El nombre es obligatorio")
	private String nombre;
	
	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "Introduce un correo válido")
	private String mail;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$",
			message = "La contraseña debe tener al menos una mayúscula y un símbolo"
			)
	private String password;
	
	@NotBlank(message = "La dirección es obligatoria")
    @Pattern(
        regexp = "^0x[a-fA-F0-9]{40}$",
        message = "La dirección debe ser una dirección válida de Ethereum"
    )
	private String address;
	
	//Datos autogenerados
	@JsonIgnore
	private String creationDate;
	
	private List<String> notificaciones;
	
	//Getters y setters
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(String string) {
		LocalDate fecha = LocalDate.now();
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("ddMMyyyy");
		
		this.creationDate = fecha.format(formato);
	}

	public List<String> getNotificaciones() {
		return notificaciones;
	}

	public void setNotificaciones(List<String> notificaciones) {
		this.notificaciones = notificaciones;
	}
	
	
}