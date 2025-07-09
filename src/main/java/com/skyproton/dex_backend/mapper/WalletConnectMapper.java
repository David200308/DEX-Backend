package com.skyproton.dex_backend.mapper;

import com.skyproton.dex_backend.dto.wallet.ResWalletConnectInitDTO;
import com.skyproton.dex_backend.entity.Auth;
import com.skyproton.dex_backend.tools.Crypto;
import com.skyproton.dex_backend.tools.Generator;
import org.springframework.stereotype.Component;

@Component
public class WalletConnectMapper {
    private final Crypto crypto;
    private final Generator generator;

    public WalletConnectMapper(Crypto crypto, Generator generator) {
        this.crypto = crypto;
        this.generator = generator;
    }

    public Auth mapToAuth(String walletAddress) {
        Auth auth = new Auth();
        auth.setUuid(generator.generateUuid());
        auth.setTimestamp(generator.generateTimestamp());
        auth.setWallet_address(crypto.encrypt(walletAddress));

        return auth;
    }

    public ResWalletConnectInitDTO mapToResWalletConnectInitDTO(Auth auth) {
        ResWalletConnectInitDTO res = new ResWalletConnectInitDTO();
        res.setAuthUuid(auth.getUuid());
        res.setTimestamp(auth.getTimestamp().toString());

        return res;
    }
}
