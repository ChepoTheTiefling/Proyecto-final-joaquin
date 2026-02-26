package com.example.demo.repositories;

import com.example.demo.objects.Autorizados;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorizadosRepository extends JpaRepository<Autorizados, Long> {
    boolean existsByAddresClienteAndCorreoIgnoreCase(String addresCliente, String correo);
    boolean existsByAddresClienteAndIdentificacionIgnoreCase(String addresCliente, String identificacion);
    boolean existsByAddresClienteAndAddressIgnoreCase(String addresCliente, String address);
    List<Autorizados> findByAddresCliente(String addresCliente);
    Optional<Autorizados> findByAddresClienteAndIdentificacionIgnoreCase(String addresCliente, String identificacion);
    Optional<Autorizados> findByAddresClienteAndCorreoIgnoreCase(String addresCliente, String correo);
    void deleteByAddresCliente(String addresCliente);
}
