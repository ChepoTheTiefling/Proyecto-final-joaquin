package com.example.demo.repositories;

import com.example.demo.objects.Administradores;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradoresRepository extends JpaRepository<Administradores, Long> {
    boolean existsByAddressIgnoreCase(String address);
    Optional<Administradores> findByAddressIgnoreCase(String address);
}
