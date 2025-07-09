package com.skyproton.dex_backend.controller;

import com.skyproton.dex_backend.dto.wallet.ReqWalletConnectInitDTO;
import com.skyproton.dex_backend.dto.wallet.ReqWalletConnectVerifyDTO;
import com.skyproton.dex_backend.dto.wallet.ResWalletConnectInitDTO;
import com.skyproton.dex_backend.dto.wallet.ResWalletConnectVerifyDTO;
import com.skyproton.dex_backend.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/connect")
    public ResponseEntity<ResWalletConnectInitDTO> walletConnectInit(
            @RequestBody ReqWalletConnectInitDTO reqWalletConnectInitDTO
    ) {
        if (
                reqWalletConnectInitDTO.getWalletAddress() == null ||
                        reqWalletConnectInitDTO.getWalletAddress().isEmpty()
        )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        try {
            String walletAddress = reqWalletConnectInitDTO.getWalletAddress();
            ResWalletConnectInitDTO resWalletConnectInitDTO = walletService.walletConnectInit(walletAddress);

            return ResponseEntity.status(HttpStatus.OK).body(resWalletConnectInitDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/connect/{authUuid}")
    public ResponseEntity<ResWalletConnectVerifyDTO> walletConnectVerify(
            @RequestBody ReqWalletConnectVerifyDTO reqWalletConnectVerifyDTO,
            @PathVariable String authUuid
    ) {
        if (
                reqWalletConnectVerifyDTO.getSignature() == null ||
                reqWalletConnectVerifyDTO.getSignature().isEmpty() ||
                        authUuid == null ||
                        authUuid.isEmpty()
        )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        try {
            String signature = reqWalletConnectVerifyDTO.getSignature();
            Boolean isVerified = walletService.walletConnectVerify(authUuid, signature);

            ResWalletConnectVerifyDTO res = new ResWalletConnectVerifyDTO();
            res.setIsVerified(isVerified);

            if (!isVerified) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            return ResponseEntity.status(HttpStatus.OK).body(res);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
