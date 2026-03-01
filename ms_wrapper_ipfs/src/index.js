import dotenv from "dotenv";
import express from "express";
import {
  createPinataClient,
  recoverMetadata,
  uploadMetadata,
  validateMetadataPayload
} from "./pinataClient.js";

dotenv.config();

const app = express();
const port = Number(process.env.PORT || 3001);

app.use(express.json({ limit: "1mb" }));

let pinata;
try {
  pinata = createPinataClient();
} catch (error) {
  console.error("[ms_wrapper_ipfs] Error de configuracion:", error.message);
}

app.get("/health", (_req, res) => {
  res.json({
    service: "ms_wrapper_ipfs",
    status: pinata ? "up" : "misconfigured"
  });
});

async function subirMetadataHandler(req, res) {
  try {
    if (!pinata) {
      return res.status(500).json({ error: "Servicio no configurado. Revisa variables PINATA_*" });
    }

    const validationError = validateMetadataPayload(req.body || {});
    if (validationError) {
      return res.status(400).json({ error: validationError });
    }

    console.log(`[ms_wrapper_ipfs] POST /subirMetadata idPedido=${req.body?.idPedido}`);
    const result = await uploadMetadata(pinata, req.body);
    console.log(`[ms_wrapper_ipfs] metadata subida cid=${result.cid}`);
    return res.status(201).json(result);
  } catch (error) {
    console.error("[ms_wrapper_ipfs] error subirMetadata:", error.message);
    return res.status(502).json({
      error: "Error subiendo metadata a Pinata",
      details: error.message
    });
  }
}

async function recuperarMetadataHandler(req, res) {
  try {
    if (!pinata) {
      return res.status(500).json({ error: "Servicio no configurado. Revisa variables PINATA_*" });
    }

    const cid = req.params.cid || req.query.cid;
    if (!cid) {
      return res.status(400).json({ error: "CID obligatorio" });
    }

    console.log(`[ms_wrapper_ipfs] GET /recuperarMetadata cid=${cid}`);
    const metadata = await recoverMetadata(pinata, cid);
    return res.json({
      cid,
      metadata
    });
  } catch (error) {
    console.error("[ms_wrapper_ipfs] error recuperarMetadata:", error.message);
    return res.status(502).json({
      error: "Error recuperando metadata desde Pinata/IPFS",
      details: error.message
    });
  }
}

app.post("/subirMetadata", subirMetadataHandler);
app.post("/subirMetada", subirMetadataHandler);

app.get("/recuperarMetadata/:cid", recuperarMetadataHandler);
app.get("/recuperarMetadata", recuperarMetadataHandler);

app.listen(port, () => {
  console.log(`[ms_wrapper_ipfs] escuchando en http://localhost:${port}`);
});
