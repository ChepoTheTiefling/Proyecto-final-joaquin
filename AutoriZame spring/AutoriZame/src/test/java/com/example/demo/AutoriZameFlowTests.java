package com.example.demo;

import com.example.demo.controllers.AuthController;
import com.example.demo.controllers.AutorizadosController;
import com.example.demo.controllers.EmpresasController;
import com.example.demo.controllers.PedidosController;
import com.example.demo.controllers.RepartidoresController;
import com.example.demo.controllers.UsuariosController;
import com.example.demo.objects.Autorizados;
import com.example.demo.objects.Empresas;
import com.example.demo.objects.Pedidos;
import com.example.demo.objects.Repartidores;
import com.example.demo.objects.Usuarios;
import com.example.demo.services.EmpresasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AutoriZameFlowTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private UsuariosController usuariosController;

    @Autowired
    private AutorizadosController autorizadosController;

    @Autowired
    private EmpresasController empresasController;

    @Autowired
    private RepartidoresController repartidoresController;

    @Autowired
    private PedidosController pedidosController;

    @Autowired
    private EmpresasService empresasService;

    @Test
    void mostrarDatosClienteRequiereTokenYNoRompePassword() {
        String userAddress = "0x1111111111111111111111111111111111111111";
        String userPassword = "Password!A";

        Usuarios u = new Usuarios();
        u.setNombre("Cliente Uno");
        u.setMail("cliente1@test.com");
        u.setPassword(userPassword);
        u.setAddress(userAddress);

        assertEquals(200, usuariosController.crearUsuario(u).getStatusCode().value());
        assertEquals(401, usuariosController.mostrarDatos("token-invalido").getStatusCode().value());

        String token = loginUsuario(userAddress, userPassword);
        ResponseEntity<Usuarios> mostrar = usuariosController.mostrarDatos(token);

        assertEquals(200, mostrar.getStatusCode().value());
        assertNotNull(mostrar.getBody());
        assertEquals("***********", mostrar.getBody().getPassword());

        String tokenAgain = loginUsuario(userAddress, userPassword);
        assertNotNull(tokenAgain);
    }

    @Test
    void actualizacionEmpresaNoPermiteAddressYSiPermiteTelefono() {
        String adminToken = loginAdmin();

        Empresas e = new Empresas();
        e.setNombre("FastShip");
        e.setMail("fastship@test.com");
        e.setAddress("0x2222222222222222222222222222222222222222");
        e.setTlf("600123123");
        e.setPassword("Password!A");

        assertEquals(200, empresasController.crear(adminToken, e).getStatusCode().value());

        assertEquals(400, empresasController.actualizar(adminToken, e.getAddress(), "address",
                "0x3333333333333333333333333333333333333333").getStatusCode().value());

        assertEquals(200, empresasController.actualizar(adminToken, e.getAddress(), "telefono",
                "699888777").getStatusCode().value());

        Empresas actualizada = empresasService.getByAddress(e.getAddress());
        assertNotNull(actualizada);
        assertEquals("699888777", actualizada.getTlf());
    }

    @Test
    void asignacionYCambioEstadoRespetanReglas() {
        String adminToken = loginAdmin();

        Empresas empresa = new Empresas();
        empresa.setNombre("ShipNow");
        empresa.setMail("shipnow@test.com");
        empresa.setAddress("0x4444444444444444444444444444444444444444");
        empresa.setTlf("611222333");
        empresa.setPassword("Password!A");

        assertEquals(200, empresasController.crear(adminToken, empresa).getStatusCode().value());
        String empresaToken = loginEmpresa(empresa.getAddress(), "Password!A");

        Repartidores rep1 = new Repartidores();
        rep1.setNombre("Rep Uno");
        rep1.setCorreo("rep1@test.com");
        rep1.setTlf("612345678");
        rep1.setAddress_empresa(empresa.getAddress());
        rep1.setPassword("Password!A");

        Repartidores rep2 = new Repartidores();
        rep2.setNombre("Rep Dos");
        rep2.setCorreo("rep2@test.com");
        rep2.setTlf("612345679");
        rep2.setAddress_empresa(empresa.getAddress());
        rep2.setPassword("Password!A");

        assertEquals(200, repartidoresController.registrar(empresaToken, rep1).getStatusCode().value());
        assertEquals(200, repartidoresController.registrar(empresaToken, rep2).getStatusCode().value());

        Usuarios cliente = new Usuarios();
        cliente.setNombre("Cliente Pedido");
        cliente.setMail("clientepedido@test.com");
        cliente.setPassword("Password!A");
        cliente.setAddress("0x5555555555555555555555555555555555555555");

        assertEquals(200, usuariosController.crearUsuario(cliente).getStatusCode().value());
        String clienteToken = loginUsuario(cliente.getAddress(), "Password!A");

        Autorizados autorizado = new Autorizados();
        autorizado.setNombre("Autorizado Uno");
        autorizado.setIdentificacion("AUT-001");
        autorizado.setTlf("612345670");
        autorizado.setAddress("0x6666666666666666666666666666666666666666");
        assertEquals(200, autorizadosController.crear(autorizado, clienteToken).getStatusCode().value());

        Pedidos pedido = new Pedidos();
        pedido.setDescripcion("Pedido de prueba");
        pedido.setIdAutorizado("AUT-001");
        pedido.setDireccionEntrega("Calle Falsa 123");

        assertEquals(200, pedidosController.registrar(clienteToken, pedido).getStatusCode().value());
        assertEquals(200, repartidoresController.asignar(empresaToken, 1, rep1.getCorreo()).getStatusCode().value());

        String rep2Token = loginRepartidor(rep2.getCorreo(), "Password!A");
        assertEquals(403, pedidosController.cambiarEstado(rep2Token, 1, Pedidos.Estado.Procesando).getStatusCode().value());

        String rep1Token = loginRepartidor(rep1.getCorreo(), "Password!A");
        assertEquals(400, pedidosController.cambiarEstado(rep1Token, 1, Pedidos.Estado.Entregado).getStatusCode().value());
        assertEquals(200, pedidosController.cambiarEstado(rep1Token, 1, Pedidos.Estado.Procesando).getStatusCode().value());
        assertEquals(200, pedidosController.cambiarEstado(rep1Token, 1, Pedidos.Estado.Entregado).getStatusCode().value());
    }

    @Test
    void eliminarUsuarioRequiereXConfirm() {
        Usuarios u = new Usuarios();
        u.setNombre("Cliente Borrado");
        u.setMail("clienteborrado@test.com");
        u.setPassword("Password!A");
        u.setAddress("0x7777777777777777777777777777777777777777");

        assertEquals(200, usuariosController.crearUsuario(u).getStatusCode().value());
        String token = loginUsuario(u.getAddress(), u.getPassword());

        ResponseEntity<String> primera = usuariosController.eliminarUsuario(token, null);
        assertEquals(200, primera.getStatusCode().value());
        String confirm = extractConfirmToken(primera.getBody());

        ResponseEntity<String> segunda = usuariosController.eliminarUsuario(token, confirm);
        assertEquals(200, segunda.getStatusCode().value());

        assertEquals(401, authController.login(u.getAddress(), u.getPassword()).getStatusCode().value());
    }

    @Test
    void eliminarEmpresaRequiereXConfirm() {
        String adminToken = loginAdmin();

        Empresas e = new Empresas();
        e.setNombre("DeleteShip");
        e.setMail("deleteship@test.com");
        e.setAddress("0x8888888888888888888888888888888888888888");
        e.setTlf("611999111");
        e.setPassword("Password!A");
        assertEquals(200, empresasController.crear(adminToken, e).getStatusCode().value());

        ResponseEntity<String> primera = empresasController.eliminar(adminToken, null, e.getAddress());
        assertEquals(200, primera.getStatusCode().value());
        String confirm = extractConfirmToken(primera.getBody());

        ResponseEntity<String> segunda = empresasController.eliminar(adminToken, confirm, e.getAddress());
        assertEquals(200, segunda.getStatusCode().value());
        assertEquals(null, empresasService.getByAddress(e.getAddress()));
    }

    private String loginUsuario(String address, String password) {
        ResponseEntity<String> res = authController.login(address, password);
        assertEquals(200, res.getStatusCode().value());
        return extractUuidToken(res.getBody());
    }

    private String loginEmpresa(String address, String password) {
        ResponseEntity<String> res = authController.loginEmpresa(address, password);
        assertEquals(200, res.getStatusCode().value());
        return extractUuidToken(res.getBody());
    }

    private String loginRepartidor(String correo, String password) {
        ResponseEntity<String> res = authController.loginRepartidor(correo, password);
        assertEquals(200, res.getStatusCode().value());
        return extractUuidToken(res.getBody());
    }

    private String loginAdmin() {
        ResponseEntity<String> res = authController.loginAdmin(
                "0x4960Be8B41B3210Fca5c2e592372c47ea8dc4F86",
                "admin");
        assertEquals(200, res.getStatusCode().value());
        return extractUuidToken(res.getBody());
    }

    private String extractUuidToken(String text) {
        Pattern p = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        Matcher m = p.matcher(text == null ? "" : text);
        if (!m.find()) {
            throw new IllegalStateException("No se pudo extraer token UUID de: " + text);
        }
        return m.group();
    }

    private String extractConfirmToken(String text) {
        return extractUuidToken(text);
    }
}
