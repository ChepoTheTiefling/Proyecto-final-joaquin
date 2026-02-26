package com.example.demo.controllers;

import com.example.demo.objects.Autorizados;
import com.example.demo.objects.Pedidos;
import com.example.demo.services.AutorizadosService;
import com.example.demo.services.PedidosService;
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
    private final PedidosService pedidosService;

    public AutorizadosController(SesionesService sesionesService,
                                 AutorizadosService autorizadosService,
                                 PedidosService pedidosService) {

        this.sesionesService = sesionesService;
        this.autorizadosService = autorizadosService;
        this.pedidosService = pedidosService;
    }

    @PostMapping("/Crear_Autorizado")
    public ResponseEntity<String> crear(@Valid @RequestBody Autorizados a,
                                        @RequestHeader("Authorization") String token) {

        String addressCliente = sesionesService.getAddressPorToken(token);
        if (addressCliente == null)
            return ResponseEntity.status(401).body("Token invalido");

        if (autorizadosService.existsCorreoByCliente(addressCliente, a.getCorreo()))
            return ResponseEntity.status(409).body("Ya existe una persona autorizada con ese correo");

        if (autorizadosService.existsIdentificacionByCliente(addressCliente, a.getIdentificacion()))
            return ResponseEntity.status(409).body("Ya existe una persona autorizada con esa identificación");

        if (autorizadosService.existsAddressByCliente(addressCliente, a.getAddress()))
            return ResponseEntity.status(409).body("Ya existe una persona autorizada con esa dirección");

        autorizadosService.crearAutorizado(addressCliente, a);
        return ResponseEntity.ok("Autorizado creado con exito");
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

    @PatchMapping("/Actualizar_Autorizado")
    public ResponseEntity<String> actualizar(@RequestHeader("Authorization") String token,
                                             @RequestParam String identificacion,
                                             @RequestParam String dato,
                                             @RequestParam String mod) {

        String addressCliente = sesionesService.getAddressPorToken(token);
        if (addressCliente == null)
            return ResponseEntity.status(401).body("Token invalido");

        Autorizados a = autorizadosService.getByClienteAndIdentificacion(addressCliente, identificacion);

        if (a == null)
            return ResponseEntity.status(404).body("Autorizado no encontrado");

        switch (dato.toLowerCase()) {
            case "nombre" -> a.setNombre(mod);
            case "telefono" -> a.setTlf(mod);
            case "correo" -> {
                if (!mod.equalsIgnoreCase(a.getCorreo()) &&
                        autorizadosService.existsCorreoByCliente(addressCliente, mod))
                    return ResponseEntity.status(409).body("Correo ya en uso");

                a.setCorreo(mod);
            }
            case "address", "direccion" -> {
                if (!mod.equalsIgnoreCase(a.getAddress()) &&
                        autorizadosService.existsAddressByCliente(addressCliente, mod))
                    return ResponseEntity.status(409).body("Dirección ya en uso");

                a.setAddress(mod);
            }
            case "identificacion" -> {
                return ResponseEntity.status(400).body("La identificación no se puede modificar");
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

        // eliminar referencias en pedidos
        Autorizados autor = autorizadosService.getByClienteAndIdentificacion(addressCliente, identificacion);

        if (autor != null) {
            for (Pedidos p : pedidosService.getAll()) {
                if (p.getAddressesAutorizados() != null) {
                    p.getAddressesAutorizados().remove(autor.getAddress());
                }
            }
        }

        boolean eliminado = autorizadosService.eliminarAutorizado(addressCliente, identificacion);
        sesionesService.limpiarTokenTemporal(token);

        if (eliminado)
            return ResponseEntity.ok("Autorizado eliminado");

        return ResponseEntity.status(404).body("Autorizado no encontrado");
    }
}
