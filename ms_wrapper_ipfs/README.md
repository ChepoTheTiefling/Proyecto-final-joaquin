# ms_wrapper_ipfs

Microservicio REST para envolver operaciones de Pinata/IPFS.

## Endpoints

- `GET /health`
- `POST /subirMetadata`
- `GET /recuperarMetadata/:cid`

## Ejecutar

1. Copiar `.env.example` a `.env` y rellenar valores.
2. Instalar dependencias:
   - `npm install`
3. Arrancar:
   - `npm start`

## Ejemplo POST /subirMetadata

```json
{
  "idPedido": 123,
  "addressCliente": "0x1111111111111111111111111111111111111111",
  "addressAutorizado": "0x2222222222222222222222222222222222222222",
  "timestamp": "26/02/2026 18:30:00"
}
```
