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

    public RestBlockchainNftGateway(@Value("${wrappers.sc.base-url}") String baseUrl) {
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
            System.out.println("[SPRING->SC] mint pedidoId=" + pedidoId + " to=" + ownerAddress);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/mintarAutorizacion")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            BlockchainTxResult result = new BlockchainTxResult();
            result.setChainTokenId(parseLong(response == null ? null : response.get("chainTokenId")));
            result.setTxHash(response == null ? null : readAsString(response.get("txHash")));
            System.out.println("[SPRING<-SC] mint chainTokenId=" + result.getChainTokenId() + " txHash=" + result.getTxHash());
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo ejecutar mint en ms_wrapper_sc", ex);
        }
    }

    @Override
    public String transferAuthorizationToken(long chainTokenId, String fromAddress, String toAddress, String senderPrivateKey) {
        if (senderPrivateKey == null || senderPrivateKey.isBlank()) {
            throw new IllegalArgumentException("senderPrivateKey es obligatorio para transferir");
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("tokenId", chainTokenId);
            payload.put("toAddress", toAddress);
            payload.put("senderPrivateKey", senderPrivateKey);
            System.out.println("[SPRING->SC] transfer tokenId=" + chainTokenId + " to=" + toAddress);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/transferirAutorizacion")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
            String txHash = response == null ? null : readAsString(response.get("txHash"));
            System.out.println("[SPRING<-SC] transfer txHash=" + txHash);
            return txHash;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo ejecutar transferencia en ms_wrapper_sc", ex);
        }
    }

    @Override
    public String burnAuthorizationToken(long chainTokenId, String ownerAddress, String senderPrivateKey) {
        if (senderPrivateKey == null || senderPrivateKey.isBlank()) {
            throw new IllegalArgumentException("senderPrivateKey es obligatorio para quemar");
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("tokenId", chainTokenId);
            payload.put("senderPrivateKey", senderPrivateKey);
            System.out.println("[SPRING->SC] burn tokenId=" + chainTokenId);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/quemarAutorizacion")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
            String txHash = response == null ? null : readAsString(response.get("txHash"));
            System.out.println("[SPRING<-SC] burn txHash=" + txHash);
            return txHash;
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
