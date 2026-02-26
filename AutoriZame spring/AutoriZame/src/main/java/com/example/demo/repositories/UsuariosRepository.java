package com.example.demo.repositories;

import com.example.demo.objects.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByAddress(String address);
    boolean existsByAddressIgnoreCase(String address);
    boolean existsByMailIgnoreCase(String mail);
    void deleteByAddress(String address);
}
