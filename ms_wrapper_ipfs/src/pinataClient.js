import { PinataSDK } from "pinata";

const ETH_ADDRESS_REGEX = /^0x[a-fA-F0-9]{40}$/;

export function createPinataClient() {
  const pinataJwt = process.env.PINATA_JWT;
  const pinataGateway = process.env.PINATA_GATEWAY;

  if (!pinataJwt) {
    throw new Error("Falta PINATA_JWT en variables de entorno");
  }
  if (!pinataGateway) {
    throw new Error("Falta PINATA_GATEWAY en variables de entorno");
  }

  return new PinataSDK({ pinataJwt, pinataGateway });
}

export function validateMetadataPayload(body) {
  const required = ["idPedido", "addressCliente", "addressAutorizado", "timestamp"];
  const missing = required.filter((field) => body[field] === undefined || body[field] === null || body[field] === "");

  if (missing.length > 0) {
    return `Faltan campos obligatorios: ${missing.join(", ")}`;
  }

  if (!ETH_ADDRESS_REGEX.test(String(body.addressCliente))) {
    return "addressCliente no es una direccion Ethereum valida";
  }

  if (!ETH_ADDRESS_REGEX.test(String(body.addressAutorizado))) {
    return "addressAutorizado no es una direccion Ethereum valida";
  }

  return null;
}

export async function uploadMetadata(pinata, payload) {
  const fileName = `pedido-${payload.idPedido}.json`;
  const upload = await pinata.upload.public.json(payload);
  const cid = upload?.cid;
  if (!cid) {
    throw new Error("La respuesta de Pinata no contiene CID");
  }
  const ipfsUrl = buildIpfsUrl(cid);

  return {
    fileName,
    cid,
    tokenUri: `ipfs://${cid}`,
    ipfsUrl,
    raw: upload
  };
}

export async function recoverMetadata(pinata, cid) {
  const response = await pinata.gateways.public.get(cid);
  return response?.data ?? response;
}

function buildIpfsUrl(cid) {
  const gateway = process.env.PINATA_GATEWAY || "";
  const normalized = gateway.startsWith("http") ? gateway : `https://${gateway}`;
  return `${normalized.replace(/\/+$/, "")}/ipfs/${cid}`;
}
