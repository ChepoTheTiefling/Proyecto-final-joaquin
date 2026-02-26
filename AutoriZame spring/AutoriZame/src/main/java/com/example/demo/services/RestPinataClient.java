package com.example.demo.services;

import com.example.demo.objects.PinataUploadResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.Map;

@Service
@ConditionalOnProperty(name = "wrappers.enabled", havingValue = "true")
public class RestPinataClient implements PinataClient {

    private final RestClient restClient;

    public RestPinataClient(@Value("${wrappers.pinata.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public PinataUploadResult uploadJson(String fileName, String jsonContent) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/subirMetadata")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonContent)
                    .retrieve()
                    .body(Map.class);

            PinataUploadResult result = new PinataUploadResult();
            result.setCid(readAsString(response, "cid"));
            result.setIpfsUrl(readAsString(response, "ipfsUrl"));
            result.setTokenUri(readAsString(response, "tokenUri"));
            return result;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo subir metadata usando ms_wrapper_ipfs", ex);
        }
    }

    private String readAsString(Map<String, Object> map, String key) {
        if (map == null) return null;
        Object value = map.get(key);
        return value == null ? null : value.toString();
    }
}
