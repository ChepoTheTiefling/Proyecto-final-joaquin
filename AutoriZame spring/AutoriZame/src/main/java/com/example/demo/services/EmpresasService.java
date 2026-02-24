package com.example.demo.services;

import com.example.demo.objects.Empresas;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpresasService {

    private final List<Empresas> empresas = new ArrayList<>();

    public List<Empresas> getAll() {
        return empresas;
    }

    public boolean existsAddress(String address) {
        return empresas.stream().anyMatch(e -> address.equalsIgnoreCase(e.getAddress()));
    }

    public boolean existsNombre(String nombre) {
        return empresas.stream().anyMatch(e -> nombre.equalsIgnoreCase(e.getNombre()));
    }

    public void crearEmpresa(Empresas e) {
        empresas.add(e);
    }

    public Empresas getByAddress(String address) {
        if (address == null) return null;
        return empresas.stream()
                .filter(e -> address.equals(e.getAddress()))
                .findFirst()
                .orElse(null);
    }

    public boolean eliminarEmpresa(String address) {
        return empresas.removeIf(e -> address.equalsIgnoreCase(e.getAddress()));
    }
}
