# ms_wrapper_sc

Microservicio REST para operaciones NFT sobre el contrato `AutoriZameToken` usando `ethers`.

## Endpoints

- `GET /health`
- `POST /mintarAutorizacion`
- `POST /transferirAutorizacion`
- `POST /quemarAutorizacion`
- `GET /owner/:tokenId`

## Ejecutar

1. Copiar `.env.example` a `.env` y rellenar valores.
2. Instalar dependencias:
   - `npm install`
3. Arrancar:
   - `npm start`

## Ejemplo POST /mintarAutorizacion

```json
{
  "pedidoId": 123,
  "toAddress": "0x1111111111111111111111111111111111111111",
  "tokenUri": "ipfs://bafy..."
}
```

## Ejemplo POST /transferirAutorizacion

```json
{
  "tokenId": 1,
  "toAddress": "0x2222222222222222222222222222222222222222",
  "senderPrivateKey": "0x..."
}
```
