// SPDX-License-Identifier: MIT
pragma solidity ^0.8.27;

import {ERC721} from "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import {ERC721Burnable} from "@openzeppelin/contracts/token/ERC721/extensions/ERC721Burnable.sol";
import {ERC721Enumerable} from "@openzeppelin/contracts/token/ERC721/extensions/ERC721Enumerable.sol";
import {ERC721URIStorage} from "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import {Ownable} from "@openzeppelin/contracts/access/Ownable.sol";
import {ReentrancyGuard} from "@openzeppelin/contracts/utils/ReentrancyGuard.sol";

contract AutoriZameToken is
    ERC721,
    ERC721Enumerable,
    ERC721URIStorage,
    ERC721Burnable,
    Ownable,
    ReentrancyGuard
{
    uint256 private _nextTokenId;

    enum EstadoPedido {
        Procesando,
        Enviando,
        Entregado,
        Cancelado
    }

    // Errores personalizados
    error CorreoDuplicado();
    error FaltaDato(string campo);
    error NoAutorizado();
    error IdentificacionDuplicada();
    error EmpresaNoExiste();
    error RepartidorNoActivo();
    error EstadoInvalido();
    error NoPendiente();
    error DireccionInvalida();
    error PedidoNoPerteneceAEmpresa();
    error PedidoNoPerteneceACliente();
    error PedidoNoPerteneceARepartidor();
    error ErrorPersonalizado(string motivo);

    // Eventos
    event ClienteRegistrado(address cliente);
    event PersonaAutorizadaRegistrada(address cliente, string identificacion);
    event PedidoCreado(uint256 tokenId, address cliente);
    event PedidoAsignado(uint256 tokenId, address empresa, address repartidor);
    event EstadoPedidoActualizado(uint256 tokenId, string nuevoEstado);
    event PedidoEntregado(uint256 tokenId);
    event AutorizacionTransferida(
        uint256 idPedido,
        uint256 idToken,
        address from,
        address to
    );
    event AutorizacionQuemada(
        uint256 idPedido,
        uint256 idToken,
        address by
    );

    // Structs
    struct Cliente {
        string nombre;
        string correo;
        uint256 fechaRegistro;
        bool existe;
    }

    struct PersonaAutorizada {
        string nombre;
        string identificacion;
        string telefono;
        bool existe;
    }

    struct EmpresaRepartidora {
        string nombre;
        string correo;
        string telefono;
        bool existe;
    }

    struct Repartidor {
        string nombre;
        string correo;
        string telefono;
        bool activo;
        address empresa;
        bool existe;
    }

    struct Pedido {
        uint256 tokenId;
        string descripcion;
        string destinatarioId;
        string direccion;
        address cliente;
        EstadoPedido estado;
        address autorizado;
        address empresa;
        address repartidor;
    }

    // Mappings
    mapping(address => Cliente) internal clientes;
    mapping(address => mapping(bytes32 => PersonaAutorizada))
        internal autorizados;
    mapping(address => EmpresaRepartidora) internal empresasPorDireccion;
    mapping(bytes32 => address) internal nombreAEmpresa;
    mapping(address => Repartidor) internal repartidores;
    mapping(uint256 => Pedido) internal pedidos;
    mapping(address => uint256[]) internal pedidosDeCliente;
    mapping(address => uint256[]) internal pedidosDeAutorizado;

    constructor(
        address initialOwner
    ) ERC721("AutoriZameToken", "AZT") Ownable(initialOwner) {}

    // Función safeMint corregida
    function safeMint(
        address to,
        string memory uri
    ) public onlyOwner returns (uint256) {
        if (to == address(0)) revert DireccionInvalida();
        uint256 tokenId = _nextTokenId++;
        _safeMint(to, tokenId);
        _setTokenURI(tokenId, uri);
        return tokenId;
    }

    // Overrides de ERC721
    function _update(
        address to,
        uint256 tokenId,
        address auth
    ) internal override(ERC721, ERC721Enumerable) returns (address) {
        return super._update(to, tokenId, auth);
    }

    function _increaseBalance(
        address account,
        uint128 value
    ) internal override(ERC721, ERC721Enumerable) {
        super._increaseBalance(account, value);
    }

    function tokenURI(
        uint256 tokenId
    ) public view override(ERC721, ERC721URIStorage) returns (string memory) {
        return super.tokenURI(tokenId);
    }

    function supportsInterface(
        bytes4 interfaceId
    )
        public
        view
        override(ERC721, ERC721Enumerable, ERC721URIStorage)
        returns (bool)
    {
        return super.supportsInterface(interfaceId);
    }

    // Modificadores
    modifier soloCliente() {
        if (!clientes[msg.sender].existe) revert NoAutorizado();
        _;
    }

    modifier soloEmpresa() {
        if (!empresasPorDireccion[msg.sender].existe) revert NoAutorizado();
        _;
    }

    modifier soloRepartidor() {
        if (
            !repartidores[msg.sender].existe || !repartidores[msg.sender].activo
        ) revert NoAutorizado();
        _;
    }

    modifier existeCliente(address c) {
        if (!clientes[c].existe) revert NoAutorizado();
        _;
    }

    modifier existeEmpresa(address empresa) {
        if (!empresasPorDireccion[empresa].existe) revert EmpresaNoExiste();
        _;
    }

    modifier estadoValido(uint256 tokenId, EstadoPedido nuevoEstado) {
        Pedido storage pedido = pedidos[tokenId];

        if (
            pedido.estado == EstadoPedido.Entregado ||
            pedido.estado == EstadoPedido.Cancelado
        ) {
            revert NoPendiente();
        }

        if (
            pedido.estado == EstadoPedido.Procesando &&
            nuevoEstado != EstadoPedido.Enviando
        ) {
            revert EstadoInvalido();
        }

        if (
            pedido.estado == EstadoPedido.Enviando &&
            nuevoEstado != EstadoPedido.Entregado &&
            nuevoEstado != EstadoPedido.Cancelado
        ) {
            revert EstadoInvalido();
        }

        _;
    }

    // Funciones de clientes
    function registrarCliente(
        string memory nombre,
        string memory correo
    ) public {
        if (bytes(correo).length == 0) revert FaltaDato("correo");
        if (clientes[msg.sender].existe) revert CorreoDuplicado();

        clientes[msg.sender] = Cliente({
            nombre: nombre,
            correo: correo,
            fechaRegistro: block.timestamp,
            existe: true
        });
        emit ClienteRegistrado(msg.sender);
    }

    function verCliente(address wallet) public view returns (Cliente memory) {
        return clientes[wallet];
    }

    function actualizarCliente(
        string memory nombre,
        string memory nuevoCorreo
    ) public soloCliente {
        if (bytes(nuevoCorreo).length == 0) revert FaltaDato("correo");
        clientes[msg.sender].nombre = nombre;
        clientes[msg.sender].correo = nuevoCorreo;
    }

    function eliminarCliente() public soloCliente {
        delete clientes[msg.sender];
    }

    // Funciones de personas autorizadas
    function registrarPersonaAutorizada(
        string memory nombre,
        string memory identificacion,
        string memory telefono
    ) public soloCliente {
        if (bytes(identificacion).length == 0)
            revert FaltaDato("identificacion");
        bytes32 idBytes = keccak256(bytes(identificacion));
        if (autorizados[msg.sender][idBytes].existe)
            revert IdentificacionDuplicada();

        autorizados[msg.sender][idBytes] = PersonaAutorizada({
            nombre: nombre,
            identificacion: identificacion,
            telefono: telefono,
            existe: true
        });
        emit PersonaAutorizadaRegistrada(msg.sender, identificacion);
    }

    function listarAutorizados()
        public
        view
        soloCliente
        returns (PersonaAutorizada[] memory)
    {
        // Implementación básica
        return new PersonaAutorizada[](0);
    }

    function actualizarAutorizado(
        string memory identificacion,
        string memory nuevoNombre,
        string memory nuevoTelefono
    ) public soloCliente {
        bytes32 idBytes = keccak256(bytes(identificacion));
        if (!autorizados[msg.sender][idBytes].existe) revert NoAutorizado();
        autorizados[msg.sender][idBytes].nombre = nuevoNombre;
        autorizados[msg.sender][idBytes].telefono = nuevoTelefono;
    }

    function eliminarAutorizado(
        string memory identificacion
    ) public soloCliente {
        bytes32 idBytes = keccak256(bytes(identificacion));
        if (!autorizados[msg.sender][idBytes].existe) revert NoAutorizado();
        delete autorizados[msg.sender][idBytes];
    }

    // Funciones de empresas
    function registrarEmpresa(
        string memory nombre,
        string memory correo,
        string memory telefono
    ) public {
        if (bytes(nombre).length == 0) revert FaltaDato("nombre");
        bytes32 nombreBytes = keccak256(bytes(nombre));
        if (
            nombreAEmpresa[nombreBytes] != address(0) &&
            empresasPorDireccion[nombreAEmpresa[nombreBytes]].existe
        ) {
            revert CorreoDuplicado();
        }

        empresasPorDireccion[msg.sender] = EmpresaRepartidora({
            nombre: nombre,
            correo: correo,
            telefono: telefono,
            existe: true
        });

        nombreAEmpresa[nombreBytes] = msg.sender;
    }

    function listarEmpresas()
        public
        pure
        returns (EmpresaRepartidora[] memory)
    {
        return new EmpresaRepartidora[](0);
    }

    function actualizarEmpresa(
        string memory newCorreo,
        string memory newTelefono
    ) public soloEmpresa {
        empresasPorDireccion[msg.sender].correo = newCorreo;
        empresasPorDireccion[msg.sender].telefono = newTelefono;
    }

    function eliminarEmpresa() public soloEmpresa {
        delete empresasPorDireccion[msg.sender];
    }

    // Funciones de repartidores
    function registrarRepartidor(
        address repartidor,
        string memory nombre,
        string memory correo,
        string memory telefono
    ) public soloEmpresa {
        if (bytes(nombre).length == 0) revert FaltaDato("nombre");
        if (repartidores[repartidor].existe) revert CorreoDuplicado(); // Evita duplicados

        repartidores[repartidor] = Repartidor({
            nombre: nombre,
            correo: correo,
            telefono: telefono,
            activo: true,
            empresa: msg.sender, // La empresa que lo registra (msg.sender)
            existe: true
        });
    }

    function listarRepartidores() public pure returns (Repartidor[] memory) {
        return new Repartidor[](0);
    }

    function actualizarRepartidor(
        string memory nuevoCorreo,
        string memory nuevoTelefono
    ) public soloRepartidor {
        repartidores[msg.sender].correo = nuevoCorreo;
        repartidores[msg.sender].telefono = nuevoTelefono;
    }

    function eliminarRepartidor() public soloRepartidor {
        delete repartidores[msg.sender];
    }

    // Funciones de pedidos (NFT)
    function crearPedido(
        string memory descripcion,
        string memory destinatarioId,
        string memory direccion,
        string memory ipfsMetadata
    ) public soloCliente returns (uint256) {
        if (bytes(direccion).length == 0) revert FaltaDato("direccion");

        uint256 tokenId = _nextTokenId++;
        _safeMint(msg.sender, tokenId);
        _setTokenURI(tokenId, ipfsMetadata);

        pedidos[tokenId] = Pedido({
            tokenId: tokenId,
            descripcion: descripcion,
            destinatarioId: destinatarioId,
            direccion: direccion,
            cliente: msg.sender,
            estado: EstadoPedido.Procesando,
            autorizado: address(0),
            empresa: address(0),
            repartidor: address(0)
        });

        pedidosDeCliente[msg.sender].push(tokenId);
        emit PedidoCreado(tokenId, msg.sender);
        return tokenId;
    }

    function asignarPedido(
        uint256 tokenId,
        address repartidor
    ) public soloEmpresa {
        Pedido storage pedido = pedidos[tokenId];

        if (pedido.estado != EstadoPedido.Procesando) revert EstadoInvalido();
        if (
            !repartidores[repartidor].existe || !repartidores[repartidor].activo
        ) revert RepartidorNoActivo();
        if (repartidores[repartidor].empresa != msg.sender)
            revert PedidoNoPerteneceAEmpresa();

        pedido.repartidor = repartidor;
        pedido.estado = EstadoPedido.Enviando;
        pedido.empresa = msg.sender;
        emit PedidoAsignado(tokenId, msg.sender, repartidor);
    }

    function verPedidosCliente(
        address walletCliente
    ) public view existeCliente(walletCliente) returns (uint256[] memory) {
        return pedidosDeCliente[walletCliente];
    }

    function verPedidosAutorizado(
        address autorizado
    ) public view returns (uint256[] memory) {
        return pedidosDeAutorizado[autorizado];
    }

    function actualizarEstado(
        uint256 tokenId,
        EstadoPedido nuevoEstado
    ) public soloRepartidor estadoValido(tokenId, nuevoEstado) {
        pedidos[tokenId].estado = nuevoEstado;
        emit EstadoPedidoActualizado(
            tokenId,
            _convertirEstadoAString(nuevoEstado)
        );
    }

    function entregarPedido(
        uint256 tokenId
    ) public soloRepartidor nonReentrant {
        Pedido storage pedido = pedidos[tokenId];

        if (pedido.repartidor != msg.sender)
            revert PedidoNoPerteneceARepartidor();
        if (pedido.estado != EstadoPedido.Enviando) revert EstadoInvalido();

        pedido.estado = EstadoPedido.Entregado;
        _transfer(pedido.cliente, pedido.empresa, tokenId);
        emit PedidoEntregado(tokenId);
    }

    function cancelarPedido(uint256 tokenId) public soloCliente {
        Pedido storage pedido = pedidos[tokenId];

        if (pedido.cliente != msg.sender) revert PedidoNoPerteneceACliente();
        if (pedido.estado != EstadoPedido.Procesando) revert EstadoInvalido();

        pedido.estado = EstadoPedido.Cancelado;
        _burn(tokenId);
    }

    // Transferencia controlada de autorizacion NFT (cliente/autorizado -> autorizado/repartidor)
    function transferirAutorizacion(
        uint256 tokenId,
        address to
    ) public nonReentrant {
        if (to == address(0)) revert DireccionInvalida();
        if (_ownerOf(tokenId) == address(0))
            revert ErrorPersonalizado("Token no existe");

        address actualOwner = ownerOf(tokenId);
        if (actualOwner != msg.sender) {
            revert ErrorPersonalizado("El token no pertenece al invocador");
        }

        safeTransferFrom(msg.sender, to, tokenId);
        emit AutorizacionTransferida(tokenId, tokenId, msg.sender, to);
    }

    // Quema controlada de autorizacion NFT (repartidor owner del token)
    function quemarAutorizacion(
        uint256 tokenId
    ) public soloRepartidor nonReentrant {
        if (_ownerOf(tokenId) == address(0))
            revert ErrorPersonalizado("Token no existe");
        if (ownerOf(tokenId) != msg.sender) {
            revert ErrorPersonalizado("Solo el owner puede quemar el token");
        }

        _burn(tokenId);
        emit AutorizacionQuemada(tokenId, tokenId, msg.sender);
    }

    // Para convertir enum a string
    function _convertirEstadoAString(
        EstadoPedido estado
    ) private pure returns (string memory) {
        if (estado == EstadoPedido.Procesando) return "Procesando";
        if (estado == EstadoPedido.Enviando) return "Enviando";
        if (estado == EstadoPedido.Entregado) return "Entregado";
        return "Cancelado";
    }
}
