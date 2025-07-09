package com.skyproton.dex_backend.dto.swap;

import lombok.Data;
import java.math.BigInteger;

@Data
public class ReqApproveDTO {
    private int chainId;
    private String tokenAddress;
    private String ownerAddress;
    private String spender;
    private BigInteger amount;
    private BigInteger gasLimit;
    private BigInteger gasPrice;
    private BigInteger maxFeePerGas;
    private BigInteger maxPriorityFeePerGas;
    private boolean eip1559;
}
