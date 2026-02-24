package com.example.demo.services;

import com.example.demo.objects.BlockchainTxResult;
import com.example.demo.objects.NftAutorizacion;
import com.example.demo.objects.PinataUploadResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class NftAutorizacionService {

    private final PinataClient pinataClient;
    private final BlockchainNftGateway blockchainNftGateway;
    private final Map<Long, NftAutorizacion> tokens = new HashMap<>();
    private long nextTokenId = 1L;
    private final SecureRandom random = new SecureRandom();

    public NftAutorizacionService(PinataClient pinataClient, BlockchainNftGateway blockchainNftGateway) {
        this.pinataClient = pinataClient;
        this.blockchainNftGateway = blockchainNftGateway;
    }

    public synchronized NftAutorizacion mintParaPedido(int pedidoId,
                                                       String ownerAddress,
                                                       String idAutorizado,
                                                       String direccionEntrega,
                                                       String descripcion) {
        validarAddress(ownerAddress);
        if (idAutorizado == null || idAutorizado.isBlank()) {
            throw new IllegalArgumentException("El ID de autorizado es obligatorio");
        }

        long tokenId = nextTokenId++;
        NftAutorizacion nft = new NftAutorizacion();
        nft.setTokenId(tokenId);
        nft.setPedidoId(pedidoId);
        nft.setOwnerAddress(ownerAddress);
        nft.setCodigoNumerico(generarCodigo8Digitos());
        nft.setIdAutorizadoHash(hashConSalt(idAutorizado, "autorizame-salt"));

        String metadataJson = crearMetadataJson(nft, direccionEntrega, descripcion);
        PinataUploadResult upload = pinataClient.uploadJson("pedido-" + pedidoId + ".json", metadataJson);
        nft.setCidIpfs(upload.getCid());
        nft.setMetadataUri(upload.getTokenUri());
        nft.setMetadataJson(metadataJson);

        BlockchainTxResult mintTx = blockchainNftGateway.mintAuthorizationToken(ownerAddress, nft.getMetadataUri(), pedidoId);
        nft.setChainTokenId(mintTx.getChainTokenId());
        nft.setMintTxHash(mintTx.getTxHash());

        tokens.put(tokenId, nft);
        return nft;
    }

    public synchronized NftAutorizacion transferirAutorizacion(long tokenId, String fromAddress, String toAddress) {
        validarAddress(fromAddress);
        validarAddress(toAddress);
        NftAutorizacion nft = getToken(tokenId);
        if (nft.getEstado() == NftAutorizacion.Estado.QUEMADO) {
            throw new IllegalStateException("El token esta quemado");
        }
        if (!fromAddress.equalsIgnoreCase(nft.getOwnerAddress())) {
            throw new IllegalStateException("El token no pertenece al emisor");
        }

        String txHash = blockchainNftGateway.transferAuthorizationToken(nft.getChainTokenId(), fromAddress, toAddress);
        nft.setOwnerAddress(toAddress);
        nft.setTransferTxHash(txHash);
        return nft;
    }

    public synchronized NftAutorizacion quemarAutorizacion(long tokenId, String ownerAddress) {
        validarAddress(ownerAddress);
        NftAutorizacion nft = getToken(tokenId);
        if (nft.getEstado() == NftAutorizacion.Estado.QUEMADO) {
            throw new IllegalStateException("El token ya esta quemado");
        }
        if (!ownerAddress.equalsIgnoreCase(nft.getOwnerAddress())) {
            throw new IllegalStateException("Solo el owner puede quemar el token");
        }

        String txHash = blockchainNftGateway.burnAuthorizationToken(nft.getChainTokenId(), ownerAddress);
        nft.setEstado(NftAutorizacion.Estado.QUEMADO);
        nft.setBurnTxHash(txHash);
        return nft;
    }

    public synchronized NftAutorizacion getToken(long tokenId) {
        NftAutorizacion nft = tokens.get(tokenId);
        if (nft == null) {
            throw new IllegalArgumentException("Token no encontrado");
        }
        return nft;
    }

    private String generarCodigo8Digitos() {
        int value = random.nextInt(100_000_000);
        return String.format("%08d", value);
    }

    private String hashConSalt(String value, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((value + "|" + salt).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }

    private String crearMetadataJson(NftAutorizacion nft, String direccionEntrega, String descripcion) {
        return "{"
                + "\"name\":\"" + escapeJson("AutoriZame Pedido #" + nft.getPedidoId()) + "\"," 
                + "\"description\":\"" + escapeJson("Autorizacion NFT para recogida de pedido") + "\"," 
                + "\"createdAt\":\"" + escapeJson(Instant.now().toString()) + "\"," 
                + "\"attributes\":{"
                + "\"pedidoId\":" + nft.getPedidoId() + ","
                + "\"tokenId\":" + nft.getTokenId() + ","
                + "\"codigo\":\"" + escapeJson(nft.getCodigoNumerico()) + "\"," 
                + "\"idAutorizadoHash\":\"" + escapeJson(nft.getIdAutorizadoHash()) + "\"," 
                + "\"direccionEntrega\":\"" + escapeJson(String.valueOf(direccionEntrega)) + "\"," 
                + "\"descripcionPedido\":\"" + escapeJson(String.valueOf(descripcion)) + "\""
                + "}"
                + "}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void validarAddress(String address) {
        if (address == null || !address.matches("^0x[a-fA-F0-9]{40}$")) {
            throw new IllegalArgumentException("Address Ethereum invalida");
        }
    }
}
