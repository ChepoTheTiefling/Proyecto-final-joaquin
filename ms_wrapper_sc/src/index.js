import dotenv from "dotenv";
import express from "express";
import {
  burnAuthorizationToken,
  getCurrentOwner,
  mintAuthorizationToken,
  transferAuthorizationToken
} from "./blockchainClient.js";

dotenv.config();

const app = express();
const port = Number(process.env.PORT || 3002);

app.use(express.json({ limit: "1mb" }));

app.get("/health", (_req, res) => {
  res.json({
    service: "ms_wrapper_sc",
    status: "up"
  });
});

app.post("/mintarAutorizacion", async (req, res) => {
  try {
    const { toAddress, tokenUri, pedidoId } = req.body || {};
    const result = await mintAuthorizationToken({ toAddress, tokenUri, pedidoId });
    return res.status(201).json(result);
  } catch (error) {
    const status = error.status || 502;
    return res.status(status).json({
      error: "Error en mintarAutorizacion",
      details: error.message
    });
  }
});

app.post("/transferirAutorizacion", async (req, res) => {
  try {
    const { tokenId, toAddress, senderPrivateKey } = req.body || {};
    const result = await transferAuthorizationToken({ tokenId, toAddress, senderPrivateKey });
    return res.json(result);
  } catch (error) {
    const status = error.status || 502;
    return res.status(status).json({
      error: "Error en transferirAutorizacion",
      details: error.message
    });
  }
});

app.post("/quemarAutorizacion", async (req, res) => {
  try {
    const { tokenId, senderPrivateKey } = req.body || {};
    const result = await burnAuthorizationToken({ tokenId, senderPrivateKey });
    return res.json(result);
  } catch (error) {
    const status = error.status || 502;
    return res.status(status).json({
      error: "Error en quemarAutorizacion",
      details: error.message
    });
  }
});

app.get("/owner/:tokenId", async (req, res) => {
  try {
    const result = await getCurrentOwner(req.params.tokenId);
    return res.json(result);
  } catch (error) {
    const status = error.status || 502;
    return res.status(status).json({
      error: "Error consultando owner del token",
      details: error.message
    });
  }
});

app.listen(port, () => {
  console.log(`[ms_wrapper_sc] escuchando en http://localhost:${port}`);
});
