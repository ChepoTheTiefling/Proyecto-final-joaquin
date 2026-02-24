package com.example.demo.services;

import com.example.demo.objects.BlockchainTxResult;

public interface BlockchainNftGateway {
    BlockchainTxResult mintAuthorizationToken(String ownerAddress, String tokenUri, int pedidoId);

    String transferAuthorizationToken(long chainTokenId, String fromAddress, String toAddress);

    String burnAuthorizationToken(long chainTokenId, String ownerAddress);
}
