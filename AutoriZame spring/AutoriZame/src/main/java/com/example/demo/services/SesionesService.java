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

    // token -> address/correo
    private final Map<String, String> sesionesActivas = new HashMap<>();
    // token temporal usado para confirmaciones (borrados, cierre de sesión, etc.)
    private String temptoken = null;

    public SesionesService(PrivateService privateService) {
        this.privateService = privateService;
    }

    // ---------- LOGIN USUARIO ----------
    public ResponseEntity<String> loginUsuario(String address, String password) {
        Usuarios u = privateService.getUsuarioPorAddress(address);
        if (u != null && password != null && password.equals(u.getPassword())) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, address);
            return ResponseEntity.ok("Token de sesión: " + token + " | Notificaciones: " + u.getNotificaciones());
        }
        return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
    }

    // ---------- LOGIN EMPRESA ----------
    public ResponseEntity<String> loginEmpresa(String address, String password) {
        Empresas e = privateService.getEmpresaPorAddress(address);
        if (e != null && password != null && password.equals(e.getPassword())) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, address);
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Empresa o contraseña incorrectos");
    }

    // ---------- LOGIN REPARTIDOR ----------
    public ResponseEntity<String> loginRepartidor(String correo, String password) {
        Repartidores r = privateService.getRepartidor(correo);
        if (r != null && password != null && password.equals(r.getPassword())) {
            String token = UUID.randomUUID().toString();
            sesionesActivas.put(token, correo);
            return ResponseEntity.ok("Token de sesión: " + token + " | Notificaciones: " + r.getNotificaciones());
        }
        return ResponseEntity.status(404).body("Repartidor no encontrado o contraseña incorrecta");
    }

    // ---------- CERRAR SESIÓN (equivalente a Cerrar_Sesion) ----------
    public ResponseEntity<String> cerrarSesion(String token, String confirmarHeader) {
        String address = sesionesActivas.get(token);
        if (address == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        if (confirmarHeader == null || confirmarHeader.equals("")) {
            // primera llamada: generamos token temporal
            temptoken = UUID.randomUUID().toString();
            return ResponseEntity.ok("Para confirmar la eliminación usa este token: X-Confirm: " + temptoken);
        }

        if (confirmarHeader.equals(temptoken)) {
            logoff(token);
            temptoken = null;
            return ResponseEntity.ok("Sesión cerrada");
        }

        return ResponseEntity.badRequest().body("El token de confirmación es incorrecto");
    }

    // ---------- UTILIDADES DE SESIÓN USADAS POR OTROS SERVICIOS O CONTROLLERS ----------

    public String getAddressPorToken(String token) {
        return sesionesActivas.get(token);
    }

    public boolean tokenValido(String token) {
        return sesionesActivas.containsKey(token);
    }

    public String crearTokenTemporal() {
        temptoken = UUID.randomUUID().toString();
        return temptoken;
    }

    public boolean esTokenTemporalValido(String confirmar) {
        return temptoken != null && temptoken.equals(confirmar);
    }

    public void limpiarTokenTemporal() {
        temptoken = null;
    }

    public void logoff(String token) {
        sesionesActivas.remove(token);
    }
}
