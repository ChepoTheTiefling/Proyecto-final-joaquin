package com.example.demo.services;

import com.example.demo.objects.Empresas;
import com.example.demo.objects.Repartidores;
import com.example.demo.objects.Usuarios;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SesionesService {

    private final PrivateService privateService;
    private final AdminService adminService;

    // token -> address/correo
    private final Map<String, String> sesionesActivas = new HashMap<>();
    // token de sesion -> token temporal de confirmacion
    private final Map<String, String> tokensTemporales = new HashMap<>();

    public SesionesService(PrivateService privateService, AdminService adminService) {
        this.privateService = privateService;
        this.adminService = adminService;
    }

    // ---------- LOGIN USUARIO ----------
    public ResponseEntity<String> loginUsuario(String address, String password) {
        Usuarios u = privateService.getUsuarioPorAddress(address);
        if (u != null && password != null && password.equals(u.getPassword())) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, address);
            return ResponseEntity.ok("Token de sesion: " + token + " | Notificaciones: " + u.getNotificaciones());
        }
        return ResponseEntity.status(401).body("Usuario o contrasena incorrectos");
    }

    // ---------- LOGIN EMPRESA ----------
    public ResponseEntity<String> loginEmpresa(String address, String password) {
        Empresas e = privateService.getEmpresaPorAddress(address);
        if (e != null && password != null && password.equals(e.getPassword())) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, address);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Empresa o contrasena incorrectos");
    }

    // ---------- LOGIN REPARTIDOR ----------
    public ResponseEntity<String> loginRepartidor(String correo, String password) {
        Repartidores r = privateService.getRepartidor(correo);
        if (r != null && password != null && password.equals(r.getPassword())) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, correo);
            return ResponseEntity.ok("Token de sesion: " + token + " | Notificaciones: " + r.getNotificaciones());
        }
        return ResponseEntity.status(401).body("Repartidor o contrasena incorrectos");
    }

    // ---------- LOGIN ADMIN ----------
    public ResponseEntity<String> loginAdmin(String address, String password) {
        boolean valido = adminService.getAll().stream()
                .anyMatch(a -> a.getAddress().equalsIgnoreCase(address) && a.getPassword().equals(password));

        if (valido) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, address);
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(401).body("Administrador o contrasena incorrectos");
    }

    // ---------- CERRAR SESION (equivalente a Cerrar_Sesion) ----------
    public ResponseEntity<String> cerrarSesion(String token, String confirmarHeader) {
        String address = sesionesActivas.get(token);
        if (address == null) {
            return ResponseEntity.status(401).body("Token invalido");
        }

        if (confirmarHeader == null || confirmarHeader.equals("")) {
            String tokenTemporal = crearTokenTemporal(token);
            return ResponseEntity.ok("Para confirmar la eliminacion usa este token: X-Confirm: " + tokenTemporal);
        }

        if (esTokenTemporalValido(token, confirmarHeader)) {
            logoff(token);
            return ResponseEntity.ok("Sesion cerrada");
        }

        return ResponseEntity.badRequest().body("El token de confirmacion es incorrecto");
    }

    // ---------- UTILIDADES DE SESION USADAS POR OTROS SERVICIOS O CONTROLLERS ----------

    public String getAddressPorToken(String token) {
        return sesionesActivas.get(token);
    }

    public boolean tokenValido(String token) {
        return sesionesActivas.containsKey(token);
    }

    public String crearTokenTemporal(String tokenSesion) {
        String tokenTemporal = UUID.randomUUID().toString();
        tokensTemporales.put(tokenSesion, tokenTemporal);
        return tokenTemporal;
    }

    public boolean esTokenTemporalValido(String tokenSesion, String confirmar) {
        String tokenTemporalGuardado = tokensTemporales.get(tokenSesion);
        return tokenTemporalGuardado != null && tokenTemporalGuardado.equals(confirmar);
    }

    public void limpiarTokenTemporal(String tokenSesion) {
        tokensTemporales.remove(tokenSesion);
    }

    public void logoff(String token) {
        sesionesActivas.remove(token);
        limpiarTokenTemporal(token);
    }
}

