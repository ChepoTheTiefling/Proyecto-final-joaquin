package com.example.demo.controllers;

import com.example.demo.services.*;
import com.example.demo.objects.Empresas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class EmpresasController {

    private final EmpresasService empresasService;
    private final SesionesService sesionesService;
    private final AdminService adminService;
    private final RepartidoresService repartidoresService;

    public EmpresasController(EmpresasService empresasService,
                              SesionesService sesionesService,
                              AdminService adminService,
                              RepartidoresService repartidoresService) {

        this.empresasService = empresasService;
        this.sesionesService = sesionesService;
        this.adminService = adminService;
        this.repartidoresService = repartidoresService;
    }

    // ---------------------------------------------------------
    // CREAR EMPRESA REPARTIDORA
    // ---------------------------------------------------------
    @PostMapping("/Crear_Empresa_Repartidora")
    public ResponseEntity<String> crear(@Valid @RequestBody Empresas e) {

        if (empresasService.existsAddress(e.getAddress()))
            return ResponseEntity.status(409).body("Ya hay una empresa con esa dirección");

        if (empresasService.existsNombre(e.getNombre()))
            return ResponseEntity.status(409).body("Ya hay una empresa con ese nombre");

        empresasService.crearEmpresa(e);
        return ResponseEntity.ok("Empresa creada con éxito");
    }

    // ---------------------------------------------------------
    // CONSULTAR EMPRESAS
    // ---------------------------------------------------------
    @GetMapping("/Consulta_Empresas_Reparto")
    public ResponseEntity<?> consultarEmpresas() {
        return ResponseEntity.ok(empresasService.getAll());
    }

    // ---------------------------------------------------------
    // ACTUALIZAR EMPRESA
    // ---------------------------------------------------------
    @PatchMapping("/Actualizacion_Empresa")
    public ResponseEntity<String> actualizar(@RequestHeader("Authorization") String token,
                                             @RequestParam String address,
                                             @RequestParam String dato,
                                             @RequestParam String mod) {

        String adminAddr = sesionesService.getAddressPorToken(token);

        if (adminAddr == null)
            return ResponseEntity.status(401).body("Administrador no encontrado");

        if (!adminService.isAdmin(adminAddr))
            return ResponseEntity.status(401).body("Administrador no autorizado");

        Empresas e = empresasService.getByAddress(address);
        if (e == null)
            return ResponseEntity.status(404).body("Empresa no encontrada");

        switch (dato.toLowerCase()) {
            case "nombre" -> {
                if (empresasService.existsNombre(mod))
                    return ResponseEntity.status(409).body("Nombre en uso");

                e.setNombre(mod);
            }
            case "email" -> e.setMail(mod);
            case "address" -> e.setAddress(mod);
            default -> {
                return ResponseEntity.status(400).body("Dato incorrecto");
            }
        }

        return ResponseEntity.ok("Empresa actualizada");
    }

    // ---------------------------------------------------------
    // ELIMINAR EMPRESA
    // ---------------------------------------------------------
    @DeleteMapping("/Eliminar_Empresa")
    public ResponseEntity<String> eliminar(@RequestHeader("Authorization") String token,
                                           @RequestHeader(value = "X-Confirm", required = false) String confirmar,
                                           @RequestParam String address) {

        String adminAddr = sesionesService.getAddressPorToken(token);

        if (adminAddr == null)
            return ResponseEntity.status(401).body("Administrador no encontrado");

        if (!adminService.isAdmin(adminAddr))
            return ResponseEntity.status(401).body("No autorizado");

        if (confirmar == null || confirmar.equals("")) {
            String temp = sesionesService.crearTokenTemporal();
            return ResponseEntity.ok("Para confirmar la eliminación utilice el token: X-Confirm: " + temp);
        }

        if (!sesionesService.esTokenTemporalValido(confirmar))
            return ResponseEntity.badRequest().body("Token temporal incorrecto");

        // borrar repartidores asociados
        repartidoresService.getAll().removeIf(r -> address.equals(r.getAddress_empresa()));

        boolean eliminado = empresasService.eliminarEmpresa(address);

        sesionesService.limpiarTokenTemporal();

        if (eliminado)
            return ResponseEntity.ok("Empresa eliminada");

        return ResponseEntity.status(404).body("Empresa no encontrada");
    }
}
