package com.example.demo.repositories;

import com.example.demo.objects.Empresas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresasRepository extends JpaRepository<Empresas, Long> {
    boolean existsByAddressIgnoreCase(String address);
    boolean existsByNombreIgnoreCase(String nombre);
    Optional<Empresas> findByAddress(String address);
    void deleteByAddressIgnoreCase(String address);
}
