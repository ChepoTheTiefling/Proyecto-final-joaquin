package com.example.demo.services;

import com.example.demo.objects.*;
import com.example.demo.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrivateService {

    private final UsuariosService usuariosService;
    private final EmpresasService empresasService;
    private final RepartidoresService repartidoresService;
    private final AutorizadosService autorizadosService;
    private final PedidosService pedidosService;

    public PrivateService(UsuariosService usuariosService,
                          EmpresasService empresasService,
                          RepartidoresService repartidoresService,
                          AutorizadosService autorizadosService,
                          PedidosService pedidosService) {

        this.usuariosService = usuariosService;
        this.empresasService = empresasService;
        this.repartidoresService = repartidoresService;
        this.autorizadosService = autorizadosService;
        this.pedidosService = pedidosService;
    }

    // ---------------------------------------------------------
    // GETTERS NECESARIOS PARA LOS CONTROLLERS
    // ---------------------------------------------------------

    public RepartidoresService getRepartidoresService() {
        return repartidoresService;
    }

    public AutorizadosService getAutorizadosService() {
        return autorizadosService;
    }

    public PedidosService getPedidosService() {
        return pedidosService;
    }

    // ---------------------------------------------------------
    // MÉTODOS AUXILIARES (antes eran privados en tu controller)
    // ---------------------------------------------------------

    public Usuarios getUsuarioPorAddress(String address) {
        if (address == null) return null;
        try {
            return usuariosService.getByAddress(address);
        } catch (ResourceNotFoundException ex) {
            return null;
        }
    }

    public Empresas getEmpresaPorAddress(String address) {
        if (address == null) return null;
        return empresasService.getByAddress(address);
    }

    public Repartidores getRepartidor(String correo) {
        if (correo == null) return null;
        return repartidoresService.getByCorreo(correo);
    }

    public Pedidos getPedidoPorId(int id) {
        return pedidosService.getById(id);
    }

    public Autorizados getAutorizado(String addressCliente, String identificacion) {
        if (addressCliente == null || identificacion == null) return null;
        return autorizadosService.getByClienteAndIdentificacion(addressCliente, identificacion);
    }

    public boolean correoExiste(String mail) {
        if (mail == null) return false;

        boolean enClientes = usuariosService.getAll().stream()
                .anyMatch(u -> mail.equals(u.getMail()));

        boolean enRepartidores = repartidoresService.getAll().stream()
                .anyMatch(r -> mail.equals(r.getCorreo()));

        return enClientes || enRepartidores;
    }

    public void notificacionesRepartidor(String notif, String correo) {
        Repartidores r = repartidoresService.getByCorreo(correo);
        if (r != null) {
            if (r.getNotificaciones() == null)
                r.setNotificaciones(new java.util.ArrayList<>());
            r.getNotificaciones().add(notif);
            repartidoresService.actualizarRepartidor(r);
        }
    }

    public void actualizarUsuario(Usuarios usuario) {
        usuariosService.actualizarUsuario(usuario);
    }
}
