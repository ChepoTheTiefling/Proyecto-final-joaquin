package com.example.demo.controllers;

import com.example.demo.services.*;
import com.example.demo.objects.Autorizados;
import com.example.demo.objects.Pedidos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class AutorizadosController {

    private final SesionesService sesionesService;
    private final AutorizadosService autorizadosService;
    private final PedidosService pedidosService;

    public AutorizadosController(SesionesService sesionesService,
                                 AutorizadosService autorizadosService,
                                 PedidosService pedidosService) {

        this.sesionesService = sesionesService;
        this.autorizadosService = autorizadosService;
        this.pedidosService = pedidosService;
    }

    // ---------------------------------------------------------
    // CREAR AUTORIZADO
    // ---------------------------------------------------------
    @PostMapping("/Crear_Autorizado")
    public ResponseEntity<String> crear(@Valid @RequestBody Autorizados a,
                                        @RequestHeader("Authorization") String token) {

        String address = sesionesService.getAddressPorToken(token);
        if (address == null)
            return ResponseEntity.status(401).body("Token inválido");

        if (autorizadosService.existsCorreoByCliente(address, a.getCorreo()))
            return ResponseEntity.status(409).body("Ya existe una persona autorizada con ese correo");

        if (autorizadosService.existsIdentificacionByCliente(address, a.getIdentificacion()))
            return ResponseEntity.status(409).body("Ya existe una persona autorizada con esa identificación");

        if (autorizadosService.existsAddressByCliente(address, a.getAddress()))
            return ResponseEntity.status(409).body("Ya existe una persona autorizada con esa dirección");

        autorizadosService.crearAutorizado(address, a);
        return ResponseEntity.ok("Autorizado creado con éxito");
    }

	// ---------------------------------------------------------
	// CONSULTAR AUTORIZADOS
	// ---------------------------------------------------------
	@GetMapping("/Consulta_Autorizado")
	public ResponseEntity<?> consultar(@RequestHeader("Authorization") String token) {

		String address = sesionesService.getAddressPorToken(token);
		if (address == null)
			return ResponseEntity.status(401).build();

		List<Autorizados> lista = autorizadosService.getByCliente(address);

		if (lista.isEmpty())
			return ResponseEntity.ok("No hay personas autorizadas registradas");

		return ResponseEntity.ok(lista);
	}

    // ---------------------------------------------------------
    // ACTUALIZAR AUTORIZADO
    // ---------------------------------------------------------
    @PatchMapping("/Actualizar_Autorizado")
    public ResponseEntity<String> actualizar(@RequestHeader("Authorization") String token,
                                             @RequestParam String correo,
                                             @RequestParam String dato,
                                             @RequestParam String mod) {

        String address = sesionesService.getAddressPorToken(token);
        if (address == null)
            return ResponseEntity.status(401).body("Token inválido");

        Autorizados a = autorizadosService.getByClienteAndCorreo(address, correo);

        if (a == null)
            return ResponseEntity.status(404).body("Autorizado no encontrado");

        switch (dato.toLowerCase()) {
            case "nombre" -> a.setNombre(mod);
            case "telefono" -> a.setTlf(mod);
            case "correo" -> {
                if (!mod.equalsIgnoreCase(a.getCorreo()) &&
                        autorizadosService.existsCorreoByCliente(address, mod))
                    return ResponseEntity.status(409).body("Correo ya en uso");

                a.setCorreo(mod);
            }
            default -> {
                return ResponseEntity.status(400).body("Tipo de dato incorrecto");
            }
        }

        return ResponseEntity.ok("Autorizado actualizado");
    }

    // ---------------------------------------------------------
    // ELIMINAR AUTORIZADO
    // ---------------------------------------------------------
    @DeleteMapping("/Eliminar_Autorizado")
    public ResponseEntity<String> eliminar(@RequestHeader("Authorization") String token,
                                           @RequestHeader(value = "X-Confirm", required = false) String confirmar,
                                           @RequestParam String correo) {

        String address = sesionesService.getAddressPorToken(token);
        if (address == null)
            return ResponseEntity.status(401).body("Token inválido");

        if (confirmar == null || confirmar.equals("")) {
            String temp = sesionesService.crearTokenTemporal();
            return ResponseEntity.ok("Para eliminar a la persona autorizada, use el token: X-Confirm: " + temp);
        }

        if (!sesionesService.esTokenTemporalValido(confirmar))
            return ResponseEntity.badRequest().body("Token temporal incorrecto");

        // eliminar referencias en pedidos
        Autorizados autor = autorizadosService.getByClienteAndCorreo(address, correo);

        if (autor != null) {
            for (Pedidos p : pedidosService.getAll()) {
                if (p.getAddressesAutorizados() != null) {
                    p.getAddressesAutorizados().remove(autor.getAddress());
                }
            }
        }

        boolean eliminado = autorizadosService.eliminarAutorizado(address, correo);

        sesionesService.limpiarTokenTemporal();

        if (eliminado)
            return ResponseEntity.ok("Autorizado eliminado");

        return ResponseEntity.status(404).body("Autorizado no encontrado");
    }
}
