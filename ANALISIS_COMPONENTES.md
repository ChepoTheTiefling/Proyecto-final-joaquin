# Analisis de Componentes (Estado Real)

## 1. Estructura principal del repositorio
- `AutoriZame spring/AutoriZame`: backend Spring Boot (API principal).
- `AutoriZame blockchain/AutoriZame.sol`: contrato Solidity.
- `ms_wrapper_ipfs`: wrapper REST para Pinata/IPFS.
- `ms_wrapper_sc`: wrapper REST para smart contract con ethers v6.
- `postman/AutoriZame_2daEval.postman_collection.json`: pruebas E2E.

## 2. Estado backend Spring
- Hay codigo fuente completo (`controllers`, `services`, `repositories`, `objects`).
- Dependencias clave en `pom.xml`:
  - `spring-boot-starter-webmvc`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `h2`
- Persistencia activa con JPA y repositorios `JpaRepository`.
- BBDD configurada en fichero H2 para persistencia entre reinicios:
  - `jdbc:h2:file:./data/autorizame...`

## 3. Integracion Spring con wrappers
- Integracion con `ms_wrapper_ipfs` por `RestClient`:
  - subida de metadata y recuperacion de `cid/tokenUri`.
- Integracion con `ms_wrapper_sc` por `RestClient`:
  - mint, transfer, burn.
- Ajuste aplicado:
  - `senderPrivateKey` en transfer/burn se envia por body JSON en endpoints Spring, no por query.

## 4. Estado wrappers
### `ms_wrapper_ipfs`
- Endpoints:
  - `GET /health`
  - `POST /subirMetadata`
  - `GET /recuperarMetadata/:cid`
- Usa SDK oficial Pinata.
- Ajuste aplicado:
  - conversion de `cid` a URL con `pinata.gateways.public.convert(cid)`.

### `ms_wrapper_sc`
- Endpoints:
  - `GET /health`
  - `POST /mintarAutorizacion`
  - `POST /transferirAutorizacion`
  - `POST /quemarAutorizacion`
  - `GET /owner/:tokenId`
- Usa `ethers` v6 (`JsonRpcProvider`, `Wallet`, `Contract`).

## 5. Estado contrato Solidity
- Contrato ERC-721 con URI storage, burn y enumerate.
- Incluye:
  - `transferirAutorizacion`
  - `quemarAutorizacion`
  - consulta de owner por `ownerOf` (estandar ERC-721).
- Hay errores personalizados y eventos para trazabilidad.

## 6. Estado funcional frente al enunciado
- Integracion de backend Spring con ambos wrappers: implementada.
- Capa de persistencia con Spring Data JPA: implementada.
- Entidades persistidas: multiples (Usuarios, Autorizados, Pedidos, Empresas, Repartidores, NftAutorizacion, etc.).
- Flujo de pedido con NFT: implementado (mint + almacenamiento de datos NFT en bbdd).

## 7. Pendientes tecnicos razonables
1. Ejecutar validacion E2E completa con wrappers reales y credenciales reales (`.env`) para dejar evidencia final.
2. Anadir trazas minimas de negocio (pedidoId, tokenId, txHash) para reforzar la demostracion en video.
3. Verificar que el owner real del token coincide con la private key usada en transfer/burn durante demo.
