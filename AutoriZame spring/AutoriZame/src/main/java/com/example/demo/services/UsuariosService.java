package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.objects.Usuarios;
import com.example.demo.repositories.UsuariosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuariosService {

    private final UsuariosRepository usuariosRepository;

    public UsuariosService(UsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
    }

    public List<Usuarios> getAll() {
        return usuariosRepository.findAll();
    }

    public Usuarios getByAddress(String address) {
        if (address == null) return null;
        return usuariosRepository.findByAddress(address)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public boolean existsAddress(String address) {
        return usuariosRepository.existsByAddressIgnoreCase(address);
    }

    public boolean existsMail(String mail) {
        return usuariosRepository.existsByMailIgnoreCase(mail);
    }

    public void crearUsuario(Usuarios u) {
        if (u.getNotificaciones() == null) u.setNotificaciones(new ArrayList<>());
        u.setCreationDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        usuariosRepository.save(u);
    }

    public void actualizarUsuario(Usuarios u) {
        usuariosRepository.save(u);
    }

    public boolean eliminarUsuario(String address) {
        Usuarios usuario = usuariosRepository.findByAddress(address).orElse(null);
        if (usuario == null) return false;
        usuariosRepository.delete(usuario);
        return true;
    }
}
