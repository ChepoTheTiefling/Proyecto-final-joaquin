package com.example.demo.services;

import com.example.demo.objects.PinataUploadResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@ConditionalOnProperty(name = "wrappers.enabled", havingValue = "false", matchIfMissing = true)
public class MockPinataClient implements PinataClient {

    @Override
    public PinataUploadResult uploadJson(String fileName, String jsonContent) {
        String cid = "bafy" + sha256Hex(fileName + "|" + jsonContent).substring(0, 28);
        PinataUploadResult result = new PinataUploadResult();
        result.setCid(cid);
        result.setIpfsUrl("https://gateway.pinata.cloud/ipfs/" + cid);
        result.setTokenUri("ipfs://" + cid);
        return result;
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se pudo generar hash SHA-256", e);
        }
    }
}

