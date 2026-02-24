package com.example.demo.services;

import com.example.demo.objects.Administradores;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final List<Administradores> admins = new ArrayList<>();

    @PostConstruct
    private void init() {
        Administradores admin = new Administradores();
        admin.setAddress("0x4960Be8B41B3210Fca5c2e592372c47ea8dc4F86");
        admin.setPassword("admin");
        admins.add(admin);
    }

    public boolean isAdmin(String address) {
        return admins.stream()
                .anyMatch(a -> a.getAddress().equalsIgnoreCase(address));
    }

    public List<Administradores> getAll() {
        return admins;
    }
}
