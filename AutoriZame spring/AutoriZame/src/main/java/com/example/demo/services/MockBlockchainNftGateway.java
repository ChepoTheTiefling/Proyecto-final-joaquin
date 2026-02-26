package com.example.demo.services;

import com.example.demo.objects.BlockchainTxResult;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@ConditionalOnProperty(name = "wrappers.enabled", havingValue = "false", matchIfMissing = true)
public class MockBlockchainNftGateway implements BlockchainNftGateway {

    private final SecureRandom random = new SecureRandom();
    private long nextChainTokenId = 10_000L;

    @Override
    public synchronized BlockchainTxResult mintAuthorizationToken(String ownerAddress, String tokenUri, int pedidoId) {
        BlockchainTxResult result = new BlockchainTxResult();
        result.setChainTokenId(nextChainTokenId++);
        result.setTxHash(randomTxHash("mint", ownerAddress, tokenUri, String.valueOf(pedidoId)));
        return result;
    }

    @Override
    public String transferAuthorizationToken(long chainTokenId, String fromAddress, String toAddress) {
        return randomTxHash("transfer", String.valueOf(chainTokenId), fromAddress, toAddress);
    }

    @Override
    public String burnAuthorizationToken(long chainTokenId, String ownerAddress) {
        return randomTxHash("burn", String.valueOf(chainTokenId), ownerAddress);
    }

    private String randomTxHash(String... parts) {
        StringBuilder sb = new StringBuilder("0x");
        sb.append(Integer.toHexString(Math.abs(String.join("|", parts).hashCode())));
        while (sb.length() < 66) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.substring(0, 66);
    }
}
