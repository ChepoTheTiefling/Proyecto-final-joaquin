package com.example.demo.services;

import com.example.demo.objects.Repartidores;
import com.example.demo.repositories.RepartidoresRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepartidoresService {

    private final RepartidoresRepository repartidoresRepository;

    public RepartidoresService(RepartidoresRepository repartidoresRepository) {
        this.repartidoresRepository = repartidoresRepository;
    }

    public List<Repartidores> getAll() {
        return repartidoresRepository.findAll();
    }

    public boolean existsCorreo(String correo) {
        return repartidoresRepository.existsByCorreoIgnoreCase(correo);
    }

    public void registrarRepartidor(String addressEmpresa, Repartidores r) {
        r.setAddressEmpresa(addressEmpresa);
        repartidoresRepository.save(r);
    }

    public void actualizarRepartidor(Repartidores repartidor) {
        repartidoresRepository.save(repartidor);
    }

    public List<Repartidores> getByEmpresa(String addressEmpresa) {
        return repartidoresRepository.findByAddressEmpresa(addressEmpresa);
    }

    public Repartidores getByCorreo(String correo) {
        if (correo == null) return null;
        return repartidoresRepository.findByCorreo(correo).orElse(null);
    }

    public boolean eliminarRepartidor(String correo, String addressEmpresa) {
        Repartidores repartidor = getByCorreo(correo);
        if (repartidor == null) return false;
        if (!addressEmpresa.equals(repartidor.getAddressEmpresa())) return false;
        repartidoresRepository.delete(repartidor);
        return true;
    }

    public void eliminarPorEmpresa(String addressEmpresa) {
        repartidoresRepository.deleteByAddressEmpresa(addressEmpresa);
    }
}
