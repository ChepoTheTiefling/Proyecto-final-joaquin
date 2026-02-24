package com.example.demo.objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Pedidos {
    private int id;

    @NotBlank
    @Pattern(
            regexp = "^0x[a-fA-F0-9]{40}$",
            message = "La direccion debe ser una direccion valida de Ethereum"
    )
    private String addressUsuario;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String idAutorizado;

    @NotBlank
    private String direccionEntrega;

    // Se asigna cuando la empresa vincula un repartidor al pedido.
    private String mailRepartidor;
    private Long tokenIdNft;
    private String codigoAutorizacion;

    public enum Estado {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getMailRepartidor() {
        return mailRepartidor;
    }

    public void setMailRepartidor(String mailRepartidor) {
        this.mailRepartidor = mailRepartidor;
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
