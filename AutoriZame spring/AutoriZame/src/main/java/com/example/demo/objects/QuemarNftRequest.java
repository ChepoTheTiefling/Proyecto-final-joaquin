package com.example.demo.objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class QuemarNftRequest {

    @NotNull
    @Positive
    private Long tokenId;

    @NotBlank
    @Pattern(
            regexp = "^(0x)?[a-fA-F0-9]{64}$",
            message = "senderPrivateKey debe tener formato valido"
    )
    private String senderPrivateKey;

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getSenderPrivateKey() {
        return senderPrivateKey;
    }

    public void setSenderPrivateKey(String senderPrivateKey) {
        this.senderPrivateKey = senderPrivateKey;
    }
}
