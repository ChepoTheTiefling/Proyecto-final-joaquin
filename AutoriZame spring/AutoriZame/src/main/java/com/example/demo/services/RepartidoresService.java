package com.example.demo.services;

import com.example.demo.objects.Repartidores;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RepartidoresService {

    private final List<Repartidores> repartidores = new ArrayList<>();

    public List<Repartidores> getAll() {
        return repartidores;
    }

    public boolean existsCorreo(String correo) {
        return repartidores.stream().anyMatch(r -> correo.equalsIgnoreCase(r.getCorreo()));
    }

    public void registrarRepartidor(String addressEmpresa, Repartidores r) {
        r.setAddress_empresa(addressEmpresa);
        repartidores.add(r);
    }

    public List<Repartidores> getByEmpresa(String addressEmpresa) {
        List<Repartidores> res = new ArrayList<>();
        for (Repartidores r : repartidores) {
            if (addressEmpresa.equals(r.getAddress_empresa())) {
                res.add(r);
            }
        }
        return res;
    }

    public Repartidores getByCorreo(String correo) {
        if (correo == null) return null;
        return repartidores.stream().filter(r -> correo.equals(r.getCorreo())).findFirst().orElse(null);
    }

    public boolean eliminarRepartidor(String correo, String addressEmpresa) {
        return repartidores.removeIf(r ->
                correo.equals(r.getCorreo()) &&
                addressEmpresa.equals(r.getAddress_empresa()));
    }
}
