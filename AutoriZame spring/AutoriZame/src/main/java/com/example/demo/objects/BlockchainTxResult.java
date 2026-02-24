package com.example.demo.objects;

public class BlockchainTxResult {
    private long chainTokenId;
    private String txHash;

    public long getChainTokenId() {
        return chainTokenId;
    }

    public void setChainTokenId(long chainTokenId) {
        this.chainTokenId = chainTokenId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }
}

