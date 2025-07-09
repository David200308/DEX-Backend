package com.skyproton.dex_backend.service.impl;

import com.skyproton.dex_backend.dto.wallet.ResWalletConnectInitDTO;
import com.skyproton.dex_backend.entity.Auth;
import com.skyproton.dex_backend.mapper.WalletConnectMapper;
import com.skyproton.dex_backend.repository.AuthRepo;
import com.skyproton.dex_backend.service.WalletService;
import com.skyproton.dex_backend.tools.Crypto;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {
    private final Crypto crypto;
    private final WalletConnectMapper walletConnectMapper;
    private final AuthRepo authRepo;

    public WalletServiceImpl(Crypto crypto, WalletConnectMapper walletConnectMapper, AuthRepo authRepo) {
        this.crypto = crypto;
        this.walletConnectMapper = walletConnectMapper;
        this.authRepo = authRepo;
    }

    @Override
    public ResWalletConnectInitDTO walletConnectInit(String walletAddress) {
        Auth auth = walletConnectMapper.mapToAuth(walletAddress);
        Auth savedAuth = authRepo.save(auth);

        return walletConnectMapper.mapToResWalletConnectInitDTO(savedAuth);
    }

    @Override
    public Boolean walletConnectVerify(
            String authUuid,
            String signature
    ) {
        Auth authRecord = authRepo.findByUuid(authUuid);

        String walletAddress = crypto.decrypt(authRecord.getWallet_address());
        String timestamp = authRecord.getTimestamp().toString();
        String message = "Wallet Address: " + walletAddress + ", Auth UUID: " + authUuid + ", Timestamp: " + timestamp;

        return crypto.signatureVerification(message, signature, walletAddress);
    }
}
