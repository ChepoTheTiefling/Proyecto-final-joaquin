package com.example.demo.objects;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class Pedidos {
	private int id;
	
	@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección debe ser una dirección válida de Ethereum"
	    )
	private String addressUsuario;
	
	@NotBlank
	@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección Destinatario debe ser una dirección válida de Ethereum"
	    )
	private String destinatario;

	@NotBlank
	private String descripcion;

	private String idAutorizado;

	private String direccionEntrega;
	
	@Email
	private String mailRepartidor;

	private Long tokenIdNft;

	private String codigoAutorizacion;
	
	@Valid
	public List<@Pattern(
	        regexp = "^0x[a-fA-F0-9]{40}$",
	        message = "La dirección de los autorizados debe ser una dirección válida de Ethereum"
	    ) String> addressesAutorizados;		
	public enum Estado{
		Pendiente,
		Procesando,
		Entregado
	}
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
