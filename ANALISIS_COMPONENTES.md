# Comprobación de lectura de archivos (Blockchain + Spring)

## Archivos disponibles y revisados
- `AutoriZame blockchain/AutoriZame.sol`
- `AutoriZame spring/Especificación OPENAPI (api-docs.json).docx` (contenido OpenAPI embebido en el DOCX)

> Nota importante: en el repositorio actual **no hay código fuente Java/Kotlin de Spring** (por ejemplo `src/main/java/...`) ni fichero de build (`pom.xml` o `build.gradle`).

## Parte blockchain (contrato Solidity)
- El contrato `AutoriZameToken` implementa un flujo de pedidos como NFT (ERC-721) con extensiones de enumeración, URI storage y burn. Usa `Ownable` y `ReentrancyGuard`.
- Roles modelados: cliente, autorizado, empresa repartidora y repartidor.
- Flujo principal del pedido:
  1. Cliente crea pedido (`crearPedido`) y se mina NFT.
  2. Empresa asigna repartidor (`asignarPedido`).
  3. Repartidor actualiza estado (`actualizarEstado`) y entrega (`entregarPedido`).
  4. Cliente puede cancelar mientras esté en `Procesando` (`cancelarPedido`).
- Estados soportados por enum: `Procesando`, `Enviando`, `Entregado`, `Cancelado`.
- Hay validaciones con errores personalizados y modificadores de acceso (`soloCliente`, `soloEmpresa`, `soloRepartidor`).

## Parte Spring (lo que sí se puede validar con lo subido)
- El DOCX contiene una especificación OpenAPI 3.0.1 para servidor local `http://localhost:8080`.
- Endpoints de negocio identificados bajo `/autorizame/...`, por ejemplo:
  - `POST /autorizame/Crear_Usuario`
  - `POST /autorizame/Crear_Empresa_Repartidora`
  - `POST /autorizame/Crear_Autorizado`
  - `POST /autorizame/Registrar_Pedido`
  - `POST /autorizame/Registrar_Repartidor`
  - `POST /autorizame/Cerrar_Sesion`
- En varios endpoints se exige cabecera `Authorization`.
- Esquemas principales en `components.schemas`: `Usuarios`, `Empresas`, `Autorizados`, `Pedidos`, `Repartidores`, con validaciones de formato (address EVM `0x...`, teléfono de 9 dígitos, reglas de password, etc.).

## ¿Se pueden leer clases y dependencias de Spring?
**No todavía, con los archivos actuales no.**
- Para leer clases Spring necesito el código fuente (controladores, servicios, repositorios, entidades, configuración).
- Para validar dependencias necesito `pom.xml` (Maven) o `build.gradle`/`gradle.properties` (Gradle).

## Conclusión
- **Sí** se puede leer y entender la parte blockchain.
- **Sí** se puede leer la API de Spring a nivel de contrato (OpenAPI).
- **No** se pueden inspeccionar aún las clases internas ni dependencias reales de Spring porque esos archivos no están en el repositorio subido.
