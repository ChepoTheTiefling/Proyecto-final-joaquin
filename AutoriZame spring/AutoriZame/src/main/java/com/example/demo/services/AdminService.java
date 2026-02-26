package com.example.demo.services;

import com.example.demo.objects.Administradores;
import com.example.demo.repositories.AdministradoresRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdministradoresRepository administradoresRepository;

    public AdminService(AdministradoresRepository administradoresRepository) {
        this.administradoresRepository = administradoresRepository;
    }

    @PostConstruct
    private void init() {
        if (!administradoresRepository.existsByAddressIgnoreCase("0x4960Be8B41B3210Fca5c2e592372c47ea8dc4F86")) {
            Administradores admin = new Administradores();
            admin.setAddress("0x4960Be8B41B3210Fca5c2e592372c47ea8dc4F86");
            admin.setPassword("admin");
            administradoresRepository.save(admin);
        }
    }

    public boolean isAdmin(String address) {
        return administradoresRepository.existsByAddressIgnoreCase(address);
    }

    public List<Administradores> getAll() {
        return administradoresRepository.findAll();
    }
}
