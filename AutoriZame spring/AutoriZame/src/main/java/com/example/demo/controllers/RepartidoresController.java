package com.example.demo.controllers;

import com.example.demo.services.*;
import com.example.demo.objects.Pedidos;
import com.example.demo.objects.Repartidores;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class RepartidoresController {

    private final SesionesService sesionesService;
    private final RepartidoresService repartidoresService;
    private final PrivateService privateService;
    private final PedidosService pedidosService;

    public RepartidoresController(SesionesService sesionesService,
                                  RepartidoresService repartidoresService,
                                  PrivateService privateService,
                                  PedidosService pedidosService) {

        this.sesionesService = sesionesService;
        this.repartidoresService = repartidoresService;
        this.privateService = privateService;
        this.pedidosService = pedidosService;
    }

    // ---------------------------------------------------------
    // REGISTRAR REPARTIDOR
    // ---------------------------------------------------------
    @PostMapping("/Registrar_Repartidor")
    public ResponseEntity<String> registrar(@RequestHeader("Authorization") String token,
                                            @Valid @RequestBody Repartidores r) {

        String empresaAddr = sesionesService.getAddressPorToken(token);
        var empresa = privateService.getEmpresaPorAddress(empresaAddr);

        if (empresa == null)
            return ResponseEntity.status(401).body("Empresa no autenticada");

        if (repartidoresService.existsCorreo(r.getCorreo()))
            return ResponseEntity.status(409).body("Correo ya en uso");

        repartidoresService.registrarRepartidor(empresa.getAddress(), r);
        return ResponseEntity.ok("Repartidor registrado con éxito");
    }

    // ---------------------------------------------------------
    // CONSULTAR REPARTIDORES
    // ---------------------------------------------------------
    @GetMapping("/Consultar_Repartidores")
    public ResponseEntity<?> consultar(@RequestHeader("Authorization") String token) {

        String empresaAddr = sesionesService.getAddressPorToken(token);
        var empresa = privateService.getEmpresaPorAddress(empresaAddr);

        if (empresa == null)
            return ResponseEntity.status(401).build();

        var lista = repartidoresService.getByEmpresa(empresa.getAddress());

        if (lista.isEmpty())
            return ResponseEntity.ok("No hay repartidores registrados");

        return ResponseEntity.ok(lista);
    }

    // ---------------------------------------------------------
    // MODIFICAR REPARTIDOR
    // ---------------------------------------------------------
    @PatchMapping("/Modificar_Repartidores")
    public ResponseEntity<String> modificar(@RequestHeader("Authorization") String token,
                                            @RequestParam String correo,
                                            @RequestParam String dato,
                                            @RequestParam String mod) {

        String empresaAddr = sesionesService.getAddressPorToken(token);
        var empresa = privateService.getEmpresaPorAddress(empresaAddr);

        if (empresa == null)
            return ResponseEntity.status(401).body("Empresa no autenticada");

        Repartidores rep = privateService.getRepartidor(correo);

        if (rep == null || !empresa.getAddress().equals(rep.getAddress_empresa()))
            return ResponseEntity.status(404).body("Repartidor no encontrado");

        switch (dato.toLowerCase()) {
            case "correo" -> {
                if (repartidoresService.existsCorreo(mod))
                    return ResponseEntity.status(409).body("Correo ya en uso");
                rep.setCorreo(mod);
            }
            case "telefono" -> rep.setTlf(mod);
            default -> {
                return ResponseEntity.status(400).body("Dato incorrecto");
            }
        }

        return ResponseEntity.ok("Repartidor actualizado");
    }

    // ---------------------------------------------------------
    // ELIMINAR REPARTIDOR
    // ---------------------------------------------------------
    @DeleteMapping("/Eliminar_Repartidores")
    public ResponseEntity<String> eliminar(@RequestHeader("Authorization") String token,
                                           @RequestHeader(value = "X-Confirm", required = false) String confirmar,
                                           @RequestParam String correo) {

        String empresaAddr = sesionesService.getAddressPorToken(token);
        var empresa = privateService.getEmpresaPorAddress(empresaAddr);

        if (empresa == null)
            return ResponseEntity.status(401).body("Empresa no autenticada");

        if (confirmar == null || confirmar.equals("")) {
            String temp = sesionesService.crearTokenTemporal(token);
            return ResponseEntity.ok("Para confirmar la eliminación, introduzca el token: X-Confirm: " + temp);
        }

        if (!sesionesService.esTokenTemporalValido(token, confirmar))
            return ResponseEntity.badRequest().body("Token temporal incorrecto");

        // borrar asignación en pedidos
        for (Pedidos p : pedidosService.getAll()) {
            if (correo.equals(p.getMailRepartidor())) {
                p.setMailRepartidor(null);
            }
        }

        boolean eliminado = repartidoresService.eliminarRepartidor(correo, empresa.getAddress());

        sesionesService.limpiarTokenTemporal(token);

        if (eliminado)
            return ResponseEntity.ok("Repartidor eliminado");

        return ResponseEntity.status(404).body("Repartidor no encontrado");
    }

    // ---------------------------------------------------------
    // ASIGNAR REPARTIDOR A PEDIDO
    // ---------------------------------------------------------
    @PatchMapping("/Asignar_Repartidor")
    public ResponseEntity<String> asignar(@RequestHeader("Authorization") String token,
                                          @RequestParam int idPedido,
                                          @RequestParam String correo) {

        String empresaAddr = sesionesService.getAddressPorToken(token);
        var empresa = privateService.getEmpresaPorAddress(empresaAddr);

        if (empresa == null)
            return ResponseEntity.status(401).body("Empresa no autenticada");

        Repartidores r = privateService.getRepartidor(correo);
        if (r == null)
            return ResponseEntity.badRequest().body("No se encuentra el repartidor");
        if (!empresa.getAddress().equals(r.getAddress_empresa()))
            return ResponseEntity.badRequest().body("El repartidor no pertenece a la empresa");
        if (r.getEstado() != Repartidores.Estado.Activo)
            return ResponseEntity.badRequest().body("El repartidor no esta activo");

        if (!empresa.getAddress().equals(r.getAddress_empresa()))
            return ResponseEntity.badRequest().body("El repartidor no pertenece a la empresa autenticada");

        if (r.getEstado() != Repartidores.Estado.Activo)
            return ResponseEntity.badRequest().body("El repartidor no está activo");

        Pedidos p = privateService.getPedidoPorId(idPedido);
        if (p == null)
            return ResponseEntity.badRequest().body("No se encuentra el pedido");
        if (p.getEstado() != Pedidos.Estado.Pendiente)
            return ResponseEntity.badRequest().body("Solo se pueden asignar pedidos en estado Pendiente");
        if (p.getMailRepartidor() != null && privateService.getRepartidor(p.getMailRepartidor()) != null)
            return ResponseEntity.badRequest().body("El pedido ya tiene un repartidor asignado");

        p.setMailRepartidor(correo);
        privateService.notificacionesRepartidor("Pedido con id " + idPedido + " asignado", correo);

        return ResponseEntity.ok("Repartidor asignado con éxito");
    }
}

