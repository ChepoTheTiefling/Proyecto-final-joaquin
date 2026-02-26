package com.example.demo.objects;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "repartidores", uniqueConstraints = {
		@UniqueConstraint(columnNames = "correo")
})
public class Repartidores {

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
	
    @Pattern(
        regexp = "^0x[a-fA-F0-9]{40}$",
        message = "La dirección debe ser una dirección válida de Ethereum"
    )
	@Column(name = "address_empresa", nullable = false)
	private String addressEmpresa;
	
	@NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).*$",
			message = "La contraseña debe tener al menos una mayúscula y un símbolo"
			)
	@Column(nullable = false)
	private String password;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "repartidores_notificaciones", joinColumns = @JoinColumn(name = "repartidor_id"))
	@Column(name = "notificacion")
	private List<String> notificaciones;
	
	public enum Estado{
		Activo,
		Inactivo
	}
	
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.Activo;

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

	public String getAddress_empresa() {
		return addressEmpresa;
	}

	public void setAddress_empresa(String address_empresa) {
		this.addressEmpresa = address_empresa;
	}

	public String getAddressEmpresa() {
		return addressEmpresa;
	}

	public void setAddressEmpresa(String addressEmpresa) {
		this.addressEmpresa = addressEmpresa;
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
