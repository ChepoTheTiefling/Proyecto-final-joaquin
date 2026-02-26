package com.example.demo.repositories;

import com.example.demo.objects.Repartidores;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepartidoresRepository extends JpaRepository<Repartidores, Long> {
    boolean existsByCorreoIgnoreCase(String correo);
    List<Repartidores> findByAddressEmpresa(String addressEmpresa);
    Optional<Repartidores> findByCorreo(String correo);
    void deleteByAddressEmpresa(String addressEmpresa);
}
