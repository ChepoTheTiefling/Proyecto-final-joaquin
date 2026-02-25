package com.example.demo.controllers;

import com.example.demo.objects.Pedidos;
import com.example.demo.objects.Usuarios;
import com.example.demo.objects.NftAutorizacion;
import com.example.demo.services.NftAutorizacionService;
import com.example.demo.services.PedidosService;
import com.example.demo.services.PrivateService;
import com.example.demo.services.SesionesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class PedidosController {

    private final SesionesService sesionesService;
    private final PrivateService privateService;
    private final PedidosService pedidosService;
    private final NftAutorizacionService nftAutorizacionService;

    public PedidosController(SesionesService sesionesService,
                             PrivateService privateService,
                             PedidosService pedidosService,
                             NftAutorizacionService nftAutorizacionService) {

        this.sesionesService = sesionesService;
        this.privateService = privateService;
        this.pedidosService = pedidosService;
        this.nftAutorizacionService = nftAutorizacionService;
    }

    @PostMapping("/Registrar_Pedido")
    public ResponseEntity<String> registrar(@RequestHeader("Authorization") String token,
                                            @Valid @RequestBody Pedidos p) {

        String address = sesionesService.getAddressPorToken(token);
        if (address == null)
            return ResponseEntity.status(401).body("Cliente no valido");

        p.setAddressUsuario(address);

        if (privateService.getAutorizado(address, p.getIdAutorizado()) == null) {
            return ResponseEntity.badRequest().body("El ID de autorizado no pertenece al cliente autenticado");
        }

        pedidosService.registrarPedido(address, p);
        NftAutorizacion nft = nftAutorizacionService.mintParaPedido(
                p.getId(),
                address,
                p.getIdAutorizado(),
                p.getDireccionEntrega(),
                p.getDescripcion());
        p.setTokenIdNft(nft.getTokenId());
        p.setCodigoAutorizacion(nft.getCodigoNumerico());
        return ResponseEntity.ok("Pedido registrado con exito");
    }

    @GetMapping("/Consultar_Pedidos")
    public ResponseEntity<?> consultar(@RequestHeader("Authorization") String token) {

        String address = sesionesService.getAddressPorToken(token);

        if (address == null)
            return ResponseEntity.status(401).build();

        var lista = pedidosService.getByUsuario(address);

        if (lista.isEmpty())
            return ResponseEntity.ok("No hay pedidos registrados");

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/Transferir_Autorizacion_NFT")
    public ResponseEntity<String> transferirNft(@RequestHeader("Authorization") String token,
                                                @RequestParam long tokenId,
                                                @RequestParam String toAddress) {
        String fromAddress = sesionesService.getAddressPorToken(token);
        if (!esSesionConAddress(fromAddress)) {
            return ResponseEntity.status(401).body("Sesion no valida para operar NFT");
        }

        try {
            nftAutorizacionService.transferirAutorizacion(tokenId, fromAddress, toAddress);
            return ResponseEntity.ok("NFT transferido");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @PostMapping("/Quemar_Autorizacion_NFT")
    public ResponseEntity<String> quemarNft(@RequestHeader("Authorization") String token,
                                            @RequestParam long tokenId) {
        String ownerAddress = sesionesService.getAddressPorToken(token);
        if (!esSesionConAddress(ownerAddress)) {
            return ResponseEntity.status(401).body("Sesion no valida para operar NFT");
        }

        try {
            nftAutorizacionService.quemarAutorizacion(tokenId, ownerAddress);
            return ResponseEntity.ok("NFT quemado");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @GetMapping("/Consultar_Autorizacion_NFT")
    public ResponseEntity<?> consultarNft(@RequestParam long tokenId) {
        try {
            return ResponseEntity.ok(nftAutorizacionService.getToken(tokenId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

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
            return ResponseEntity.status(403).body("Repartidor no autorizado");

        Pedidos p = privateService.getPedidoPorId(id);

        if (p == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");
        if (p.getMailRepartidor() == null || !correo.equals(p.getMailRepartidor()))
            return ResponseEntity.status(403).body("Solo el repartidor asignado puede cambiar el estado");

        boolean transicionValida =
                (p.getEstado() == Pedidos.Estado.Pendiente && estado == Pedidos.Estado.Procesando) ||
                        (p.getEstado() == Pedidos.Estado.Procesando && estado == Pedidos.Estado.Entregado);
        if (!transicionValida)
            return ResponseEntity.badRequest().body("Transicion de estado no valida");

        if (p.getMailRepartidor() == null || !correo.equalsIgnoreCase(p.getMailRepartidor()))
            return ResponseEntity.status(403).body("Solo el repartidor asignado puede cambiar el estado del pedido");

        p.setEstado(estado);

        Usuarios u = privateService.getUsuarioPorAddress(p.getAddressUsuario());
        if (u != null) {
            if (u.getNotificaciones() == null)
                u.setNotificaciones(new ArrayList<>());

            u.getNotificaciones().add("El pedido " + id + " ha cambiado de estado a " + estado);
        }

        return ResponseEntity.ok("Estado actualizado");
    }

    @DeleteMapping("/Eliminar_Pedido")
    public ResponseEntity<String> eliminar(@RequestHeader("Authorization") String token,
                                           @RequestHeader(value = "X-Confirm", required = false) String confirmar,
                                           @RequestParam int idPedido) {

        String address = sesionesService.getAddressPorToken(token);
        if (address == null)
            return ResponseEntity.status(401).body("Cliente no autenticado");

        Pedidos p = privateService.getPedidoPorId(idPedido);
        if (p == null)
            return ResponseEntity.status(404).body("Pedido no encontrado");

        if (!address.equals(p.getAddressUsuario()))
            return ResponseEntity.status(403).body("No autorizado para cancelar este pedido");

        if (p.getEstado() != Pedidos.Estado.Pendiente)
            return ResponseEntity.badRequest().body("Solo se pueden cancelar pedidos en estado Pendiente");

        if (confirmar == null || confirmar.equals("")) {
            String temp = sesionesService.crearTokenTemporal(token);
            return ResponseEntity.ok("Para confirmar la cancelacion use el token: X-Confirm: " + temp);
        }

        if (!sesionesService.esTokenTemporalValido(token, confirmar))
            return ResponseEntity.badRequest().body("Token temporal incorrecto");

        pedidosService.eliminarPedido(idPedido);
        sesionesService.limpiarTokenTemporal(token);

        return ResponseEntity.ok("Pedido cancelado");
    }

    private boolean esSesionConAddress(String sessionValue) {
        return sessionValue != null && sessionValue.matches("^0x[a-fA-F0-9]{40}$");
    }
}

