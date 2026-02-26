package com.example.demo.services;

import com.example.demo.objects.Empresas;
import com.example.demo.repositories.EmpresasRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpresasService {

    private final EmpresasRepository empresasRepository;

    public EmpresasService(EmpresasRepository empresasRepository) {
        this.empresasRepository = empresasRepository;
    }

    public List<Empresas> getAll() {
        return empresasRepository.findAll();
    }

    public boolean existsAddress(String address) {
        return empresasRepository.existsByAddressIgnoreCase(address);
    }

    public boolean existsNombre(String nombre) {
        return empresasRepository.existsByNombreIgnoreCase(nombre);
    }

    public void crearEmpresa(Empresas e) {
        empresasRepository.save(e);
    }

    public void actualizarEmpresa(Empresas empresa) {
        empresasRepository.save(empresa);
    }

    public Empresas getByAddress(String address) {
        if (address == null) return null;
        return empresasRepository.findByAddress(address).orElse(null);
    }

    public boolean eliminarEmpresa(String address) {
        Empresas empresa = getByAddress(address);
        if (empresa == null) return false;
        empresasRepository.delete(empresa);
        return true;
    }
}
