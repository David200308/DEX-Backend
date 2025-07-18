package com.skyproton.dex_backend.dto.swap;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ReqSwapDTO {
    private String walletAddress;
    private int fee;
    private BigInteger amountIn;
    private BigInteger amountOutMin;
    private BigInteger gasLimit;
    private BigInteger gasPrice;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private boolean eip1559;
}
