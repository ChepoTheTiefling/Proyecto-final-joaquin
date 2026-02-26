package com.example.demo.services;

import com.example.demo.objects.BlockchainTxResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "wrappers.enabled", havingValue = "true")
public class RestBlockchainNftGateway implements BlockchainNftGateway {

    private final RestClient restClient;
    private final String senderPrivateKey;

    public RestBlockchainNftGateway(@Value("${wrappers.sc.base-url}") String baseUrl,
                                    @Value("${wrappers.sc.sender-private-key:}") String senderPrivateKey) {
        this.senderPrivateKey = senderPrivateKey;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public BlockchainTxResult mintAuthorizationToken(String ownerAddress, String tokenUri, int pedidoId) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("toAddress", ownerAddress);
            payload.put("tokenUri", tokenUri);
            payload.put("pedidoId", pedidoId);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/mintarAutorizacion")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            BlockchainTxResult result = new BlockchainTxResult();
            result.setChainTokenId(parseLong(response == null ? null : response.get("chainTokenId")));
            result.setTxHash(response == null ? null : readAsString(response.get("txHash")));
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo ejecutar mint en ms_wrapper_sc", ex);
        }
    }

    @Override
    public String transferAuthorizationToken(long chainTokenId, String fromAddress, String toAddress) {
        if (senderPrivateKey == null || senderPrivateKey.isBlank()) {
            throw new IllegalStateException("Falta wrappers.sc.sender-private-key para transferir en wrapper SC");
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("tokenId", chainTokenId);
            payload.put("toAddress", toAddress);
            payload.put("senderPrivateKey", senderPrivateKey);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/transferirAutorizacion")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            return response == null ? null : readAsString(response.get("txHash"));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo ejecutar transferencia en ms_wrapper_sc", ex);
        }
    }

    @Override
    public String burnAuthorizationToken(long chainTokenId, String ownerAddress) {
        if (senderPrivateKey == null || senderPrivateKey.isBlank()) {
            throw new IllegalStateException("Falta wrappers.sc.sender-private-key para quemar en wrapper SC");
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("tokenId", chainTokenId);
            payload.put("senderPrivateKey", senderPrivateKey);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/quemarAutorizacion")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            return response == null ? null : readAsString(response.get("txHash"));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo ejecutar quema en ms_wrapper_sc", ex);
        }
    }

    private long parseLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(value.toString());
    }

    private String readAsString(Object value) {
        return value == null ? null : value.toString();
    }
}
