package com.skyproton.dex_backend.service;

import java.math.BigInteger;

public interface SwapService {
    String uniswapSwapExactInputSingle(
            int chainId,
            String privateKey,
            String tokenIn,
            String tokenOut,
            int fee,
            BigInteger amountIn,
            BigInteger amountOutMin
    ) throws Exception;

    String curveSwapExactInputSingle(
            int chainId,
            String privateKey,
            String tokenIn,
            String tokenOut,
            int fee,
            BigInteger amountIn,
            BigInteger amountOutMin
    ) throws Exception;
}
