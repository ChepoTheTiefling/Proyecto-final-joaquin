package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.objects.Usuarios;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuariosService {

    private final List<Usuarios> clientes = new ArrayList<>();

    public List<Usuarios> getAll() {
        return clientes;
    }

    public Usuarios getByAddress(String address) {
        if (address == null) return null;
        //Excepción personalizada
        return clientes.stream().filter(u -> address.equals(u.getAddress())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public boolean existsAddress(String address) {
        return clientes.stream().anyMatch(u -> address.equalsIgnoreCase(u.getAddress()));
    }

    public boolean existsMail(String mail) {
        return clientes.stream().anyMatch(u -> mail.equals(u.getMail()));
    }

    public void crearUsuario(Usuarios u) {
        if (u.getNotificaciones() == null) u.setNotificaciones(new ArrayList<>());
        u.setCreationDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        clientes.add(u);
    }

    public boolean eliminarUsuario(String address) {
        return clientes.removeIf(u -> address.equals(u.getAddress()));
    }
}
