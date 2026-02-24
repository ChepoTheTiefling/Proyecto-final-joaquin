package com.example.demo.objects;

public class PinataUploadResult {
    private String cid;
    private String ipfsUrl;
    private String tokenUri;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getIpfsUrl() {
        return ipfsUrl;
    }

    public void setIpfsUrl(String ipfsUrl) {
        this.ipfsUrl = ipfsUrl;
    }

    public String getTokenUri() {
        return tokenUri;
    }

    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }
}

