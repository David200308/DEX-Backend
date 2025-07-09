package com.skyproton.dex_backend.service;

import com.skyproton.dex_backend.dto.wallet.ResWalletConnectInitDTO;
import com.skyproton.dex_backend.dto.wallet.ResWalletConnectVerifyDTO;

public interface WalletService {
    ResWalletConnectInitDTO walletConnectInit(String walletAddress);
    Boolean walletConnectVerify(
            String authUuid,
            String signature
    );
}
