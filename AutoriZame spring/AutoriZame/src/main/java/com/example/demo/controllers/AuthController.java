package com.example.demo.controllers;

import com.example.demo.services.SesionesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/${api.version}/autorizame")
public class AuthController {

    private final SesionesService sesionesService;

    public AuthController(SesionesService sesionesService) {
        this.sesionesService = sesionesService;
    }

    @GetMapping("/Login")
    public ResponseEntity<String> login(@RequestParam String address,
                                        @RequestParam String password) {
        return sesionesService.loginUsuario(address, password);
    }

    @GetMapping("/Login_Empresa")
    public ResponseEntity<String> loginEmpresa(@RequestParam String address,
                                               @RequestParam String password) {
        return sesionesService.loginEmpresa(address, password);
    }

    @GetMapping("/Login_Repartidor")
    public ResponseEntity<String> loginRepartidor(@RequestParam String correo,
                                                  @RequestParam String password) {
        return sesionesService.loginRepartidor(correo, password);
    }

    @PostMapping("/Cerrar_Sesion")
    public ResponseEntity<String> cerrarSesion(@RequestHeader("Authorization") String token,
                                               @RequestHeader(value = "X-Confirm", required = false) String confirmar) {

        return sesionesService.cerrarSesion(token, confirmar);
    }
}
