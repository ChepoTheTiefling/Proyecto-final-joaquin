package com.example.demo.objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class Autorizados {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La identificacion es obligatoria")
    private String identificacion;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(
            regexp = "^[0-9]{9}$",
            message = "El numero debe tener 9 digitos"
    )
    private String tlf;

    @NotBlank(message = "La direccion de Ethereum es obligatoria")
    @Pattern(
            regexp = "^0x[a-fA-F0-9]{40}$",
            message = "La direccion debe ser una direccion valida de Ethereum"
    )
    private String address;

    @NotBlank
    private String addressCliente;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTlf() {
        return tlf;
    }

    public void setTlf(String tlf) {
        this.tlf = tlf;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressCliente() {
        return addressCliente;
    }

    public void setAddressCliente(String addressCliente) {
        this.addressCliente = addressCliente;
    }
}
