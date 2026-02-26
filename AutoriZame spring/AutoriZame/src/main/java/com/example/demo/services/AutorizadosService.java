package com.example.demo.services;

import com.example.demo.objects.Autorizados;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutorizadosService {

    private final List<Autorizados> autorizados = new ArrayList<>();

    public List<Autorizados> getAll() {
        return autorizados;
    }

    public boolean existsCorreoByCliente(String addressCliente, String correo) {
        return autorizados.stream().anyMatch(a ->
                addressCliente.equals(a.getAddres_cliente()) &&
                        correo.equalsIgnoreCase(a.getCorreo()));
    }

    public boolean existsIdentificacionByCliente(String addressCliente, String identificacion) {
        return autorizados.stream().anyMatch(a ->
                addressCliente.equals(a.getAddres_cliente()) &&
                        identificacion.equalsIgnoreCase(a.getIdentificacion()));
    }

    public boolean existsAddressByCliente(String addressCliente, String addressAutorizado) {
        return autorizados.stream().anyMatch(a ->
                addressCliente.equals(a.getAddres_cliente()) &&
                        addressAutorizado.equalsIgnoreCase(a.getAddress()));
    }

    public void crearAutorizado(String addressCliente, Autorizados a) {
        a.setAddres_cliente(addressCliente);
        autorizados.add(a);
    }

    public List<Autorizados> getByCliente(String addressCliente) {
        List<Autorizados> res = new ArrayList<>();
        for (Autorizados a : autorizados) {
            if (addressCliente.equals(a.getAddres_cliente())) {
                res.add(a);
            }
        }
        return res;
    }

    public Autorizados getByClienteAndIdentificacion(String addressCliente, String identificacion) {
        return autorizados.stream()
                .filter(a -> addressCliente.equals(a.getAddres_cliente())
                        && identificacion.equalsIgnoreCase(a.getIdentificacion()))
                .findFirst()
                .orElse(null);
    }

    public Autorizados getByClienteAndCorreo(String addressCliente, String correo) {
        return autorizados.stream()
                .filter(a ->
                        addressCliente.equals(a.getAddres_cliente()) &&
                                correo.equalsIgnoreCase(a.getCorreo()))
                .findFirst()
                .orElse(null);
    }

    public boolean eliminarAutorizado(String addressCliente, String identificacion) {
        return autorizados.removeIf(a ->
                addressCliente.equals(a.getAddres_cliente()) &&
                identificacion.equalsIgnoreCase(a.getIdentificacion()));
    }
}
