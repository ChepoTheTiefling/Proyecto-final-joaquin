package com.example.demo.repositories;

import com.example.demo.objects.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidosRepository extends JpaRepository<Pedidos, Integer> {
    List<Pedidos> findByAddressUsuario(String addressUsuario);
    void deleteByAddressUsuario(String addressUsuario);
}
