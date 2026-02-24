package com.example.demo.services;

import com.example.demo.objects.Pedidos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidosService {

    private final List<Pedidos> pedidos = new ArrayList<>();
    private int ultimoID = 0;

    public Pedidos getById(int id) {
        return pedidos.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void registrarPedido(String addressUsuario, Pedidos p) {
        p.setId(++ultimoID);
        p.setAddressUsuario(addressUsuario);
        pedidos.add(p);
    }

    public List<Pedidos> getByUsuario(String addressUsuario) {
        List<Pedidos> res = new ArrayList<>();
        for (Pedidos p : pedidos) {
            if (addressUsuario.equals(p.getAddressUsuario())) {
                res.add(p);
            }
        }
        return res;
    }

    public boolean eliminarPedido(int id) {
        return pedidos.removeIf(p -> p.getId() == id);
    }

    public List<Pedidos> getAll() {
        return pedidos;
    }
}
