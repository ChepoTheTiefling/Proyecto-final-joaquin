package com.example.demo.objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "autorizados", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"addres_cliente", "correo"}),
		@UniqueConstraint(columnNames = {"addres_cliente", "identificacion"}),
		@UniqueConstraint(columnNames = {"addres_cliente", "address"})
})
public class Autorizados {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "El nombre es obligatorio")
	@Column(nullable = false)
	private String nombre;
	
	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "Introduce un correo válido")
	@Column(nullable = false)
	private String correo;
	
	@NotBlank(message = "El teléfono es obligatorio")
	@Pattern(
			regexp = "^[0-9]{9}$",
			message = "El número debe tener 9 dígitos"
			)
	@Column(nullable = false)
	private String tlf;

	@NotBlank(message = "La identificación es obligatoria")
	@Column(nullable = false)
	private String identificacion;
	
	@Column(name = "addres_cliente", nullable = false)
	private String addresCliente;
	
	@NotBlank(message = "La dirección es obligatoria")
	@Pattern(
			regexp = "^0x[a-fA-F0-9]{40}$",
			message = "La dirección debe ser una dirección válida de Ethereum"
			)
	@Column(nullable = false)
	private String address;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
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
		return addresCliente;
	}

	public void setAddres_cliente(String addres_cliente) {
		this.addresCliente = addres_cliente;
	}

	public String getAddresCliente() {
		return addresCliente;
	}

	public void setAddresCliente(String addresCliente) {
		this.addresCliente = addresCliente;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
