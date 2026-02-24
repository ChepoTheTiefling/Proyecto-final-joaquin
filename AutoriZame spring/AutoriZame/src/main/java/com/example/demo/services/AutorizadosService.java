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

    public boolean existsCorreo(String correo) {
        return autorizados.stream().anyMatch(a -> correo.equals(a.getCorreo()));
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

    public Autorizados getByCorreo(String correo) {
        return autorizados.stream()
                .filter(a -> correo.equals(a.getCorreo()))
                .findFirst()
                .orElse(null);
    }

    public boolean eliminarAutorizado(String addressCliente, String correo) {
        return autorizados.removeIf(a ->
                addressCliente.equals(a.getAddres_cliente()) &&
                correo.equals(a.getCorreo()));
    }
}
