package com.example.demo.objects;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "pedidos")
public class Pedidos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección debe ser una dirección válida de Ethereum"
	    )
	@Column(nullable = false)
	private String addressUsuario;
	
	@NotBlank
	@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección Destinatario debe ser una dirección válida de Ethereum"
	    )
	@Column(nullable = false)
	private String destinatario;

	@NotBlank
	@Column(nullable = false)
	private String descripcion;

	@NotBlank
	@Column(nullable = false)
	private String idAutorizado;

	@NotBlank
	@Column(nullable = false)
	private String direccionEntrega;
	
	@Email
	private String mailRepartidor;

	private Long tokenIdNft;

	private String codigoAutorizacion;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "pedidos_autorizados", joinColumns = @JoinColumn(name = "pedido_id"))
	@Column(name = "address_autorizado")
	@Valid
	private List<@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección de los autorizados debe ser una dirección válida de Ethereum"
	    ) String> addressesAutorizados;		
	public enum Estado{
		Pendiente,
			Procesando,
			Entregado
	}
	@Enumerated(EnumType.STRING)
	private Estado estado = Estado.Pendiente;
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAddressUsuario() {
		return addressUsuario;
	}
	
	public void setAddressUsuario(String addressUsuario) {
		this.addressUsuario = addressUsuario;
	}
	
	public List<String> getAddressesAutorizados() {
		return addressesAutorizados;
	}
	
	public void setAddressesAutorizados(List<String> addressesAutorizados) {
		this.addressesAutorizados = addressesAutorizados;
	}
	
	public String getMailRepartidor() {
		return mailRepartidor;
	}
	
	public void setMailRepartidor(String mailRepartidor) {
		this.mailRepartidor = mailRepartidor;
	}
	
	public Estado getEstado() {
		return estado;
	}
	
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public String getIdAutorizado() {
		return idAutorizado;
	}

	public void setIdAutorizado(String idAutorizado) {
		this.idAutorizado = idAutorizado;
	}

	public String getDireccionEntrega() {
		return direccionEntrega;
	}

	public void setDireccionEntrega(String direccionEntrega) {
		this.direccionEntrega = direccionEntrega;
	}

	public Long getTokenIdNft() {
		return tokenIdNft;
	}

	public void setTokenIdNft(Long tokenIdNft) {
		this.tokenIdNft = tokenIdNft;
	}

	public String getCodigoAutorizacion() {
		return codigoAutorizacion;
	}

	public void setCodigoAutorizacion(String codigoAutorizacion) {
		this.codigoAutorizacion = codigoAutorizacion;
	}
}
