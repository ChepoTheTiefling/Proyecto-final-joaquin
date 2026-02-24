package com.example.demo.services;

import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    public boolean enviarConfirmacionEliminacion(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        return true;
    }
}
