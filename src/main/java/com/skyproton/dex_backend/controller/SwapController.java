package com.skyproton.dex_backend.controller;

import com.skyproton.dex_backend.dto.swap.ReqApproveDTO;
import com.skyproton.dex_backend.dto.swap.ReqSendSignedTxDTO;
import com.skyproton.dex_backend.dto.swap.ReqSwapDTO;
import com.skyproton.dex_backend.service.SwapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/swap")
public class SwapController {

    private final SwapService swapService;

    public SwapController(SwapService swapService) {
        this.swapService = swapService;
    }

    @PostMapping("/token/approve/tx/{chainId}/{tokenAddress}")
    public ResponseEntity<String> buildApproveTx(
            @RequestBody ReqApproveDTO req,
            @PathVariable int chainId,
            @PathVariable String tokenAddress
    ) {
        try {
            String txHex = swapService.buildApproveTx(
                    chainId,
                    tokenAddress,
                    req.getOwnerAddress(),
                    req.getSpender(),
                    req.getAmount(),
                    req.getGasLimit(),
                    req.getGasPrice(),
                    req.getMaxFeePerGas(),
                    req.getMaxPriorityFeePerGas(),
                    req.isEip1559()
            );
            return ResponseEntity.ok(txHex);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error building approve tx: " + e.getMessage());
        }
    }

    @PostMapping("/tx/{chainId}/{tokenIn}/{tokenOut}")
    public ResponseEntity<String> buildSwapTx(
            @RequestBody ReqSwapDTO req,
            @PathVariable int chainId,
            @PathVariable String tokenIn,
            @PathVariable String tokenOut
    ) {
        try {
            String txHex = swapService.buildUniswapV3SwapExactInputSingleTx(
                    chainId,
                    req.getWalletAddress(),
                    tokenIn,
                    tokenOut,
                    req.getFee(),
                    req.getAmountIn(),
                    req.getAmountOutMin(),
                    req.getGasLimit(),
                    req.getGasPrice(),
                    req.getMaxFeePerGas(),
                    req.getMaxPriorityFeePerGas(),
                    req.isEip1559()
            );
            return ResponseEntity.ok(txHex);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error building swap tx: " + e.getMessage());
        }
    }

    @PostMapping("/send/signedtx/{chainId}")
    public ResponseEntity<String> sendSignedTx(
            @RequestBody ReqSendSignedTxDTO req,
            @PathVariable int chainId
    ) {
        try {
            String txHash = swapService.sendSignedTransaction(req.getSignedTx(), chainId);
            return ResponseEntity.ok(txHash);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending tx: " + e.getMessage());
        }
    }

}
