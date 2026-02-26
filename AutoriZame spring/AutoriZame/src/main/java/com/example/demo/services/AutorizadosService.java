package com.example.demo.services;

import com.example.demo.objects.Autorizados;
import com.example.demo.repositories.AutorizadosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorizadosService {

    private final AutorizadosRepository autorizadosRepository;

    public AutorizadosService(AutorizadosRepository autorizadosRepository) {
        this.autorizadosRepository = autorizadosRepository;
    }

    public List<Autorizados> getAll() {
        return autorizadosRepository.findAll();
    }

    public boolean existsCorreoByCliente(String addressCliente, String correo) {
        return autorizadosRepository.existsByAddresClienteAndCorreoIgnoreCase(addressCliente, correo);
    }

    public boolean existsIdentificacionByCliente(String addressCliente, String identificacion) {
        return autorizadosRepository.existsByAddresClienteAndIdentificacionIgnoreCase(addressCliente, identificacion);
    }

    public boolean existsAddressByCliente(String addressCliente, String addressAutorizado) {
        return autorizadosRepository.existsByAddresClienteAndAddressIgnoreCase(addressCliente, addressAutorizado);
    }

    public void crearAutorizado(String addressCliente, Autorizados a) {
        a.setAddresCliente(addressCliente);
        autorizadosRepository.save(a);
    }

    public void actualizarAutorizado(Autorizados autorizado) {
        autorizadosRepository.save(autorizado);
    }

    public List<Autorizados> getByCliente(String addressCliente) {
        return autorizadosRepository.findByAddresCliente(addressCliente);
    }

    public Autorizados getByClienteAndIdentificacion(String addressCliente, String identificacion) {
        return autorizadosRepository.findByAddresClienteAndIdentificacionIgnoreCase(addressCliente, identificacion)
                .orElse(null);
    }

    public Autorizados getByClienteAndCorreo(String addressCliente, String correo) {
        return autorizadosRepository.findByAddresClienteAndCorreoIgnoreCase(addressCliente, correo).orElse(null);
    }

    public boolean eliminarAutorizado(String addressCliente, String identificacion) {
        Autorizados autorizado = getByClienteAndIdentificacion(addressCliente, identificacion);
        if (autorizado == null) return false;
        autorizadosRepository.delete(autorizado);
        return true;
    }

    public void eliminarPorCliente(String addressCliente) {
        autorizadosRepository.deleteByAddresCliente(addressCliente);
    }
}
