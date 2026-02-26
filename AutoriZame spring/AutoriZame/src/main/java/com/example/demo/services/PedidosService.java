package com.example.demo.services;

import com.example.demo.objects.Pedidos;
import com.example.demo.repositories.PedidosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidosService {

    private final PedidosRepository pedidosRepository;

    public PedidosService(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;
    }

    public Pedidos getById(int id) {
        return pedidosRepository.findById(id).orElse(null);
    }

    public void registrarPedido(String addressUsuario, Pedidos p) {
        p.setAddressUsuario(addressUsuario);
        pedidosRepository.save(p);
    }

    public void actualizarPedido(Pedidos pedido) {
        pedidosRepository.save(pedido);
    }

    public List<Pedidos> getByUsuario(String addressUsuario) {
        return pedidosRepository.findByAddressUsuario(addressUsuario);
    }

    public boolean eliminarPedido(int id) {
        if (!pedidosRepository.existsById(id)) return false;
        pedidosRepository.deleteById(id);
        return true;
    }

    public void eliminarPorUsuario(String addressUsuario) {
        pedidosRepository.deleteByAddressUsuario(addressUsuario);
    }

    public List<Pedidos> getAll() {
        return pedidosRepository.findAll();
    }
}
