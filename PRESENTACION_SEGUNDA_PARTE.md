# Presentacion Segunda Parte - Proyecto Final

## 1. Objetivo de esta segunda parte
- Integrar Spring con dos microservicios Blockchain:
  - `ms_wrapper_ipfs` (subida/lectura de metadata en Pinata/IPFS).
  - `ms_wrapper_sc` (mint, transfer, burn y owner del NFT en smart contract).
- Añadir persistencia relacional con Spring Data JPA (`JpaRepository`).

## 2. Arquitectura que hay que explicar
- `Backend Spring` expone la API funcional de negocio (`/v1/autorizame/...`).
- `ms_wrapper_ipfs` expone endpoints REST para subir/recuperar metadata.
- `ms_wrapper_sc` expone endpoints REST para operar con NFT.
- `H2` (bbdd relacional) guarda entidades del dominio y datos NFT.

Flujo principal:
1. Cliente registra pedido en Spring.
2. Spring crea metadata JSON.
3. Spring llama a `ms_wrapper_ipfs` para subir metadata.
4. Spring recibe `CID` y `tokenUri`.
5. Spring llama a `ms_wrapper_sc` para mintar NFT.
6. Spring guarda en bbdd el pedido y datos NFT (token interno, chainTokenId, txHash, cid, tokenUri, etc.).

## 3. Persistencia JPA a enseñar
- Entidades persistidas:
  - `Usuarios`, `Autorizados`, `Empresas`, `Repartidores`, `Pedidos`, `Administradores`, `NftAutorizacion`.
- Repositorios `JpaRepository` por entidad.
- Relaciones simples por campos/colecciones (`@ElementCollection` para notificaciones y autorizados por pedido).
- Consola H2 para enseñar tabla y cambios en vivo.

## 4. Endpoints clave para demo
- Spring:
  - `POST /Crear_Usuario`
  - `GET /Login`
  - `POST /Crear_Autorizado`
  - `POST /Registrar_Pedido`
  - `GET /Consultar_Pedidos`
  - `GET /Consultar_Autorizacion_NFT`
  - `POST /Transferir_Autorizacion_NFT`
  - `POST /Quemar_Autorizacion_NFT`
- Wrappers:
  - `GET /health` en ambos wrappers.
  - `POST /subirMetadata` (IPFS).
  - `POST /mintarAutorizacion`, `POST /transferirAutorizacion`, `POST /quemarAutorizacion`, `GET /owner/:tokenId` (SC).

## 5. Guion recomendado para el video
1. Levantar `ms_wrapper_ipfs` y `ms_wrapper_sc`; enseñar `GET /health`.
2. Levantar Spring y mostrar que arranca con JPA + H2.
3. Crear usuario, login y guardar token de sesion.
4. Crear autorizado.
5. Registrar pedido:
   - enseñar logs de llamada a wrappers;
   - enseñar respuesta de exito.
6. Consultar pedido y mostrar `tokenIdNft` + `codigoAutorizacion`.
7. Consultar NFT por id en Spring.
8. Mostrar bbdd en H2:
   - tabla `pedidos`;
   - tabla `nft_autorizaciones`;
   - cambios tras registrar pedido.
9. (Opcional) Transferir y quemar NFT para cerrar flujo.

## 6. Preguntas de defensa (sintesis) a preparar
- Por que usar `RestClient` en Spring para integrar wrappers.
- Diferencia entre `tokenId` interno en Spring y `chainTokenId` en blockchain.
- Que guarda exactamente la metadata y por que se sube a IPFS.
- Como validais que solo el actor correcto hace cada operacion.
- Que aporta JPA frente a listas en memoria.
- Que pasaria si un wrapper no esta disponible.
- Como se valida formato de address Ethereum y campos obligatorios.

## 7. Decisiones de diseno (explicacion corta)
- Separar responsabilidades:
  - Spring: logica de negocio y seguridad funcional.
  - Wrappers: adaptacion a Pinata y Smart Contract.
- Persistir tambien `NftAutorizacion` para trazabilidad completa.
- Mantener API sencilla, nombres claros y validaciones directas para nivel 2o DAW.

## 8. Riesgos/pendientes a mencionar con transparencia
- Para transfer/burn reales en wrapper SC, la private key enviada debe ser del owner actual del token.
- Si se usa H2 en memoria, los datos se pierden al reiniciar.
- El archivo `ANALISIS_COMPONENTES.md` esta desactualizado y conviene alinearlo con el estado real del repo.

## 9. Checklist rapido antes de entregar
- Codigo Spring + wrappers subido y funcionando.
- Variables `.env` configuradas en ambos wrappers.
- `application.properties` ajustado para base URLs de wrappers.
- Coleccion Postman ejecutada en orden.
- Video corto con:
  - invocacion API Spring;
  - llamadas a wrappers;
  - actualizacion de bbdd visible.
- Preparadas respuestas de defensa de codigo.
