# Guia Demo Postman (Video y Defensa)

## 1. Arranque de servicios
1. Levantar `ms_wrapper_ipfs`:
   - `cd ms_wrapper_ipfs`
   - `npm install`
   - `npm start`
2. Levantar `ms_wrapper_sc`:
   - `cd ms_wrapper_sc`
   - `npm install`
   - `npm start`
3. Levantar Spring:
   - `cd "AutoriZame spring/AutoriZame"`
   - `./mvnw spring-boot:run` (o `mvnw.cmd spring-boot:run` en Windows).

Parada manual:
1. Cerrar cada consola con `Ctrl + C`.

## 2. Variables que debes revisar antes de correr la coleccion
1. En wrappers:
   - `ms_wrapper_ipfs/.env`: `PINATA_JWT`, `PINATA_GATEWAY`.
   - `ms_wrapper_sc/.env`: `RPC_URL`, `CONTRACT_ADDRESS`, `OWNER_PRIVATE_KEY`.
2. En Postman collection variables:
   - `client_private_key`
   - `autorizado_private_key`
   - direcciones (`client_address`, `autorizado_address`) coherentes con esas keys.

## 3. Orden exacto de ejecucion recomendado
1. `0. Wrapper IPFS / Health IPFS`
2. `1. Wrapper Smart Contract / Health SC`
3. `2. Spring - Usuario / Crear Usuario`
4. `2. Spring - Usuario / Login Usuario`
5. `3. Spring - Autorizados y Pedidos / Crear Autorizado`
6. `3. Spring - Autorizados y Pedidos / Registrar Pedido`
7. `3. Spring - Autorizados y Pedidos / Consultar Pedidos`
8. `3. Spring - Autorizados y Pedidos / Consultar NFT`
9. `4. Spring - Admin, Empresa y Repartidor / Login Admin`
10. `4. Spring - Admin, Empresa y Repartidor / Crear Empresa Repartidora`
11. `4. Spring - Admin, Empresa y Repartidor / Login Empresa`
12. `4. Spring - Admin, Empresa y Repartidor / Registrar Repartidor`
13. `4. Spring - Admin, Empresa y Repartidor / Login Repartidor`
14. `4. Spring - Admin, Empresa y Repartidor / Asignar Repartidor a Pedido`
15. `4. Spring - Admin, Empresa y Repartidor / Cambiar Estado Pedido a Procesando`
16. `1. Wrapper Smart Contract / Transferir Autorizacion` (ya llama a Spring)
17. `1. Wrapper Smart Contract / Quemar Autorizacion` (ya llama a Spring)

## 4. Que mostrar en el video (checklist)
1. Health de wrappers (`status: up` o equivalente).
2. Registro y login de usuario con token de sesion.
3. Registro de autorizado y pedido.
4. Evidencia de NFT generado (`spring_token_id`, `chainTokenId`, `txHash`).
5. Transfer y burn del NFT con private key del owner.
6. Consola H2 (`/h2-console`) mostrando:
   - tabla `pedidos`
   - tabla `nft_autorizaciones`
   - que los datos quedan tras reiniciar (H2 en fichero).

## 5. Errores comunes y solucion rapida
1. `senderPrivateKey ... formato valido`:
   - revisar variable de Postman, debe ser hex de 64 bytes (con o sin `0x`).
2. Error de owner al transferir/quemar:
   - la key enviada no corresponde al owner actual del token.
3. `misconfigured` en wrapper IPFS:
   - falta `PINATA_JWT` o `PINATA_GATEWAY`.
4. Errores RPC en wrapper SC:
   - revisar `RPC_URL`, `CONTRACT_ADDRESS`, `OWNER_PRIVATE_KEY`.
