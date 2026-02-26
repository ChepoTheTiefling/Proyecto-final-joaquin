package com.example.demo.controllers;

import com.example.demo.services.*;
import com.example.demo.objects.Usuarios;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class UsuariosController {

	private final UsuariosService usuariosService;
	private final SesionesService sesionesService;
	private final PrivateService privateService;
	private final AutorizadosService autorizadosService;
	private final PedidosService pedidosService;
	private final CorreoService correoService;

	public UsuariosController(UsuariosService usuariosService, SesionesService sesionesService,
			PrivateService privateService, AutorizadosService autorizadosService, PedidosService pedidosService,
			CorreoService correoService) {
		this.usuariosService = usuariosService;
		this.sesionesService = sesionesService;
		this.privateService = privateService;
		this.autorizadosService = autorizadosService;
		this.pedidosService = pedidosService;
		this.correoService = correoService;
	}

	// ---------------------------------------------------------
	// CREAR USUARIO
	// ---------------------------------------------------------
	@PostMapping("/Crear_Usuario")
	public ResponseEntity<String> crearUsuario(@Valid @RequestBody Usuarios u) {

		if (usuariosService.existsAddress(u.getAddress())) {
			return ResponseEntity.status(409).body("Ya hay un usuario con esta address");
		}

		if (privateService.correoExiste(u.getMail())) {
			return ResponseEntity.status(409).body("Correo ya en uso en otra cuenta");
		}

		usuariosService.crearUsuario(u);
		return ResponseEntity.ok("Usuario creado con éxito");
	}

	// ---------------------------------------------------------
	// MOSTRAR DATOS CLIENTE
	// ---------------------------------------------------------
	@GetMapping("/Mostrar_Datos_Cliente")
	public ResponseEntity<Usuarios> mostrarDatos(@RequestHeader("Authorization") String token) {

		String address = sesionesService.getAddressPorToken(token);
		if (address == null)
			return ResponseEntity.status(401).build();

		Usuarios u = privateService.getUsuarioPorAddress(address);
		if (u == null)
			return ResponseEntity.status(404).build();

		Usuarios datos = new Usuarios();
		datos.setNombre(u.getNombre());
		datos.setMail(u.getMail());
		datos.setAddress(u.getAddress());
		datos.setCreationDate(u.getCreationDate());
		datos.setPassword("***********");
		datos.setNotificaciones(u.getNotificaciones() == null ? new ArrayList<>() : u.getNotificaciones());

		return ResponseEntity.ok(datos);
	}

	// ---------------------------------------------------------
	// ELIMINAR USUARIO
	// ---------------------------------------------------------
	@DeleteMapping("/Eliminar_Usuario")
	public ResponseEntity<String> eliminarUsuario(@RequestHeader("Authorization") String token,
			@RequestHeader(value = "X-Confirm", required = false) String confirmar) {

		String address = sesionesService.getAddressPorToken(token);
		if (address == null) {
			return ResponseEntity.status(401).body("Token inválido");
		}

		// primera fase: generar token temporal
		if (confirmar == null || confirmar.equals("")) {
			String temp = sesionesService.crearTokenTemporal(token);
			return ResponseEntity.ok("Para confirmar la eliminación usa este token: X-Confirm: " + temp);
		}

		// comprobar token temporal
		if (!sesionesService.esTokenTemporalValido(token, confirmar)) {
			return ResponseEntity.badRequest().body("El token de confirmación es incorrecto");
		}

		// ---------------------------------------------------------
		// BORRAR PEDIDOS ASOCIADOS
		// ---------------------------------------------------------
		pedidosService.eliminarPorUsuario(address);

		// ---------------------------------------------------------
		// BORRAR AUTORIZADOS ASOCIADOS
		// ---------------------------------------------------------
		autorizadosService.eliminarPorCliente(address);

		// ---------------------------------------------------------
		// BORRAR USUARIO
		// ---------------------------------------------------------
		Usuarios usuario = privateService.getUsuarioPorAddress(address);
		usuariosService.eliminarUsuario(address);
		boolean correoEnviado = correoService.enviarConfirmacionEliminacion(usuario != null ? usuario.getMail() : null);

		// cerrar la sesión
		sesionesService.logoff(token);
		sesionesService.limpiarTokenTemporal(token);

		if (correoEnviado) {
			return ResponseEntity.ok("Usuario eliminado. Eliminando datos asociados, cerrando sesión y enviando correo de confirmación...");
		}

		return ResponseEntity.ok("Usuario eliminado. Eliminando datos asociados y cerrando sesión...");
	}

	// ---------------------------------------------------------
	// MODIFICAR DATOS USUARIO
	// ---------------------------------------------------------
	@PatchMapping("/Modificar_Datos")
	public ResponseEntity<String> modificarDatos(@RequestHeader("Authorization") String token,
			@RequestParam String dato, @RequestParam String mod) {

		String address = sesionesService.getAddressPorToken(token);
		if (address == null)
			return ResponseEntity.status(401).body("Token inválido");

		Usuarios u = privateService.getUsuarioPorAddress(address);
		if (u == null)
			return ResponseEntity.status(404).body("Usuario no encontrado");

		switch (dato.toLowerCase()) {
		case "nombre":
			u.setNombre(mod);
			break;

		case "password":
			u.setPassword(mod);
			break;

		case "mail":
			if (privateService.correoExiste(mod)) {
				return ResponseEntity.status(409).body("Correo ya en uso");
			}
			u.setMail(mod);
			break;

		default:
			return ResponseEntity.status(400).body("ERROR: Tipo de dato incorrecto");
		}

		usuariosService.actualizarUsuario(u);

		return ResponseEntity.ok("Dato modificado con éxito");
	}
}



