package com.example.demo.controllers;

import com.example.demo.objects.Autorizados;
import com.example.demo.objects.Pedidos;
import com.example.demo.services.AutorizadosService;
import com.example.demo.services.PedidosService;
import com.example.demo.services.PrivateService;
import com.example.demo.services.SesionesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class AutorizadosController {

    private final SesionesService sesionesService;
    private final AutorizadosService autorizadosService;
    private final PrivateService privateService;
    private final PedidosService pedidosService;

    public AutorizadosController(SesionesService sesionesService,
                                 AutorizadosService autorizadosService,
                                 PrivateService privateService,
                                 PedidosService pedidosService) {

        this.sesionesService = sesionesService;
        this.autorizadosService = autorizadosService;
        this.privateService = privateService;
        this.pedidosService = pedidosService;
    }

    @PostMapping("/Crear_Autorizado")
    public ResponseEntity<String> crear(@Valid @RequestBody Autorizados a,
                                        @RequestHeader("Authorization") String token) {

        String addressCliente = sesionesService.getAddressPorToken(token);
        if (addressCliente == null)
            return ResponseEntity.status(401).body("Token invalido");

        if (autorizadosService.existsIdentificacionByCliente(addressCliente, a.getIdentificacion()))
            return ResponseEntity.status(409).body("Identificacion ya registrada para este cliente");

        autorizadosService.crearAutorizado(addressCliente, a);
        return ResponseEntity.ok("Autorizado creado con exito");
    }

    @GetMapping("/Consulta_Autorizado")
    public ResponseEntity<List<Autorizados>> consultar(@RequestHeader("Authorization") String token) {

        String addressCliente = sesionesService.getAddressPorToken(token);
        if (addressCliente == null)
            return ResponseEntity.status(401).build();

        List<Autorizados> lista = autorizadosService.getByCliente(addressCliente);

        if (lista.isEmpty())
            return ResponseEntity.status(404).build();

        return ResponseEntity.ok(lista);
    }

    @PatchMapping("/Actualizar_Autorizado")
    public ResponseEntity<String> actualizar(@RequestHeader("Authorization") String token,
                                             @RequestParam String identificacion,
                                             @RequestParam String dato,
                                             @RequestParam String mod) {

        String addressCliente = sesionesService.getAddressPorToken(token);
        if (addressCliente == null)
            return ResponseEntity.status(401).body("Token invalido");

        Autorizados a = privateService.getAutorizado(addressCliente, identificacion);
        if (a == null)
            return ResponseEntity.status(404).body("Autorizado no encontrado");

        switch (dato.toLowerCase()) {
            case "nombre" -> a.setNombre(mod);
            case "telefono" -> {
                if (!esTelefonoValido(mod))
                    return ResponseEntity.badRequest().body("Telefono invalido");
                a.setTlf(mod);
            }
            case "address" -> {
                if (!esAddressEthereumValida(mod))
                    return ResponseEntity.badRequest().body("Address Ethereum invalida");
                a.setAddress(mod);
            }
            case "identificacion" -> {
                return ResponseEntity.badRequest().body("La identificacion no se puede modificar");
            }
            default -> {
                return ResponseEntity.badRequest().body("Tipo de dato incorrecto");
            }
        }

        return ResponseEntity.ok("Autorizado actualizado");
    }

    @DeleteMapping("/Eliminar_Autorizado")
    public ResponseEntity<String> eliminar(@RequestHeader("Authorization") String token,
                                           @RequestHeader(value = "X-Confirm", required = false) String confirmar,
                                           @RequestParam String identificacion) {

        String addressCliente = sesionesService.getAddressPorToken(token);
        if (addressCliente == null)
            return ResponseEntity.status(401).body("Token invalido");

        if (confirmar == null || confirmar.equals("")) {
            String temp = sesionesService.crearTokenTemporal(token);
            return ResponseEntity.ok("Para eliminar a la persona autorizada, use el token: X-Confirm: " + temp);
        }

        if (!sesionesService.esTokenTemporalValido(token, confirmar))
            return ResponseEntity.badRequest().body("Token temporal incorrecto");

        Autorizados autor = privateService.getAutorizado(addressCliente, identificacion);
        if (autor != null) {
            for (Pedidos p : pedidosService.getAll()) {
                if (identificacion.equalsIgnoreCase(p.getIdAutorizado())) {
                    p.setIdAutorizado(null);
                }
            }
        }

        boolean eliminado = autorizadosService.eliminarAutorizado(addressCliente, identificacion);
        sesionesService.limpiarTokenTemporal(token);

        if (eliminado)
            return ResponseEntity.ok("Autorizado eliminado");

        return ResponseEntity.status(404).body("Autorizado no encontrado");
    }

    private boolean esTelefonoValido(String telefono) {
        return telefono != null && telefono.matches("^[0-9]{9}$");
    }

    private boolean esAddressEthereumValida(String address) {
        return address != null && address.matches("^0x[a-fA-F0-9]{40}$");
    }
}
