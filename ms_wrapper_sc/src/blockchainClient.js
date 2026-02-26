import { Contract, JsonRpcProvider, Wallet, isAddress } from "ethers";
import { AUTORIZAME_ABI } from "./contractAbi.js";

function createError(status, message) {
  const error = new Error(message);
  error.status = status;
  return error;
}

function requireEnv(name) {
  const value = process.env[name];
  if (!value) {
    throw createError(500, `Falta variable de entorno ${name}`);
  }
  return value;
}

function getProvider() {
  const rpcUrl = requireEnv("RPC_URL");
  return new JsonRpcProvider(rpcUrl);
}

function getContractAddress() {
  const contractAddress = requireEnv("CONTRACT_ADDRESS");
  if (!isAddress(contractAddress)) {
    throw createError(500, "CONTRACT_ADDRESS no es una direccion valida");
  }
  return contractAddress;
}

function getOwnerSigner() {
  const privateKey = requireEnv("OWNER_PRIVATE_KEY");
  return getSignerFromPrivateKey(privateKey);
}

function getSignerFromPrivateKey(privateKey) {
  if (!privateKey) {
    throw createError(400, "senderPrivateKey es obligatorio para esta operacion");
  }
  const provider = getProvider();
  try {
    return new Wallet(privateKey, provider);
  } catch {
    throw createError(400, "senderPrivateKey no tiene un formato valido");
  }
}

function getContract(signerOrProvider) {
  return new Contract(getContractAddress(), AUTORIZAME_ABI, signerOrProvider);
}

export async function mintAuthorizationToken({ toAddress, tokenUri, pedidoId }) {
  if (!toAddress || !isAddress(toAddress)) {
    throw createError(400, "toAddress es obligatorio y debe ser una direccion Ethereum valida");
  }
  if (!tokenUri || String(tokenUri).trim() === "") {
    throw createError(400, "tokenUri es obligatorio");
  }

  const ownerSigner = getOwnerSigner();
  const contract = getContract(ownerSigner);

  const chainTokenId = await contract.safeMint.staticCall(toAddress, tokenUri);

  const tx = await contract.safeMint(toAddress, tokenUri);
  const receipt = await tx.wait();

  return {
    pedidoId: pedidoId ?? null,
    toAddress,
    tokenUri,
    chainTokenId: chainTokenId.toString(),
    txHash: tx.hash,
    blockNumber: receipt?.blockNumber ?? null
  };
}

export async function transferAuthorizationToken({ tokenId, toAddress, senderPrivateKey }) {
  if (tokenId === undefined || tokenId === null) {
    throw createError(400, "tokenId es obligatorio");
  }
  if (!toAddress || !isAddress(toAddress)) {
    throw createError(400, "toAddress es obligatorio y debe ser una direccion Ethereum valida");
  }

  const signer = getSignerFromPrivateKey(senderPrivateKey);
  const contract = getContract(signer);
  const owner = await contract.ownerOf(tokenId);

  if (owner.toLowerCase() !== signer.address.toLowerCase()) {
    throw createError(403, "La private key indicada no corresponde al owner actual del token");
  }

  const tx = await contract.transferirAutorizacion(tokenId, toAddress);
  const receipt = await tx.wait();

  return {
    chainTokenId: String(tokenId),
    fromAddress: signer.address,
    toAddress,
    txHash: tx.hash,
    blockNumber: receipt?.blockNumber ?? null
  };
}

export async function burnAuthorizationToken({ tokenId, senderPrivateKey }) {
  if (tokenId === undefined || tokenId === null) {
    throw createError(400, "tokenId es obligatorio");
  }

  const signer = getSignerFromPrivateKey(senderPrivateKey);
  const contract = getContract(signer);
  const owner = await contract.ownerOf(tokenId);

  if (owner.toLowerCase() !== signer.address.toLowerCase()) {
    throw createError(403, "La private key indicada no corresponde al owner actual del token");
  }

  const tx = await contract.quemarAutorizacion(tokenId);
  const receipt = await tx.wait();

  return {
    chainTokenId: String(tokenId),
    ownerAddress: signer.address,
    txHash: tx.hash,
    blockNumber: receipt?.blockNumber ?? null
  };
}

export async function getCurrentOwner(tokenId) {
  if (tokenId === undefined || tokenId === null) {
    throw createError(400, "tokenId es obligatorio");
  }

  const provider = getProvider();
  const contract = getContract(provider);
  const owner = await contract.ownerOf(tokenId);

  return {
    chainTokenId: String(tokenId),
    owner
  };
}
