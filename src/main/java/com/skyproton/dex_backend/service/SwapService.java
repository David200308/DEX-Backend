package com.skyproton.dex_backend.service;

import java.math.BigInteger;

public interface SwapService {
    String buildApproveTx(
            int chainId,
            String tokenAddress,
            String ownerAddress,
            String spender,
            BigInteger amount,
            BigInteger gasLimit,
            BigInteger gasPrice,
            BigInteger maxFeePerGas,
            BigInteger maxPriorityFeePerGas,
            boolean eip1559
    ) throws Exception;

    String buildUniswapV3SwapExactInputSingleTx(
            int chainId,
            String walletAddress,
            String tokenIn,
            String tokenOut,
            int fee,
            BigInteger amountIn,
            BigInteger amountOutMin,
            BigInteger gasLimit,
            BigInteger gasPrice,
            BigInteger maxFeePerGas,
            BigInteger maxPriorityFeePerGas,
            boolean eip1559
    ) throws Exception;

    String sendSignedTransaction(String signedTxHex, int chainId) throws Exception;
}
