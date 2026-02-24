package com.example.demo.controllers;

import com.example.demo.services.*;
import com.example.demo.objects.Pedidos;
import com.example.demo.objects.Usuarios;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class PedidosController {

    private final SesionesService sesionesService;
    private final PrivateService privateService;
    private final PedidosService pedidosService;

    public PedidosController(SesionesService sesionesService,
                             PrivateService privateService,
                             PedidosService pedidosService) {

        this.sesionesService = sesionesService;
        this.privateService = privateService;
        this.pedidosService = pedidosService;
    }

    // ---------------------------------------------------------
    // REGISTRAR PEDIDO
    // ---------------------------------------------------------
    @PostMapping("/Registrar_Pedido")
    public ResponseEntity<String> registrar(@RequestHeader("Authorization") String token,
                                            @Valid @RequestBody Pedidos p) {

        String address = sesionesService.getAddressPorToken(token);
        if (address == null)
            return ResponseEntity.status(401).body("Cliente no válido");

        if (p.getAddressesAutorizados() == null)
            p.setAddressesAutorizados(new ArrayList<>());

        p.setAddressUsuario(address);

        // validación igual que el código original
        if (!p.getAddressUsuario().equals(p.getDestinatario()) &&
            !p.getAddressesAutorizados().contains(p.getDestinatario())) {

            return ResponseEntity.badRequest().body("El destinatario debe ser el cliente o un usuario autorizado (address)");
        }

        pedidosService.registrarPedido(address, p);
        return ResponseEntity.ok("Pedido registrado con éxito");
    }

    // ---------------------------------------------------------
    // CONSULTAR PEDIDOS DEL USUARIO
    // ---------------------------------------------------------
    @GetMapping("/Consultar_Pedidos")
    public ResponseEntity<?> consultar(@RequestHeader("Authorization") String token) {

        String address = sesionesService.getAddressPorToken(token);

        if (address == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(pedidosService.getByUsuario(address));
    }

    // ---------------------------------------------------------
    // CAMBIAR ESTADO PEDIDO
    // ---------------------------------------------------------
    @PatchMapping("/Cambiar_Estado_Pedido")
    public ResponseEntity<String> cambiarEstado(@RequestHeader("Authorization") String token,
                                                @RequestParam int id,
                                                @RequestParam Pedidos.Estado estado) {

        String correo = sesionesService.getAddressPorToken(token);

        boolean esRepartidor = privateService.getRepartidoresService()
                .getAll()
                .stream()
                .anyMatch(r -> r.getCorreo().equals(correo));

        if (!esRepartidor)
            return ResponseEntity.badRequest().body("Repartidor no registrado");

        Pedidos p = privateService.getPedidoPorId(id);

        if (p == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");

        p.setEstado(estado);

        Usuarios u = privateService.getUsuarioPorAddress(p.getAddressUsuario());
        if (u != null) {
            if (u.getNotificaciones() == null)
                u.setNotificaciones(new ArrayList<>());

            u.getNotificaciones().add("El pedido " + id + " ha cambiado de estado a " + estado);
        }

        return ResponseEntity.ok("Estado actualizado");
    }

    // ---------------------------------------------------------
    // ELIMINAR PEDIDO
    // ---------------------------------------------------------
    @DeleteMapping("/Eliminar_Pedido")
    public ResponseEntity<String> eliminar(@RequestHeader("Authorization") String token,
                                           @RequestParam int idPedido) {

        String correo = sesionesService.getAddressPorToken(token);

        boolean esRepartidor = privateService.getRepartidoresService()
                .getAll()
                .stream()
                .anyMatch(r -> r.getCorreo().equals(correo));

        if (!esRepartidor)
            return ResponseEntity.badRequest().body("Repartidor no registrado");

        Pedidos p = privateService.getPedidoPorId(idPedido);
        if (p == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");

        pedidosService.eliminarPedido(idPedido);

        return ResponseEntity.ok("Pedido eliminado");
    }
}
