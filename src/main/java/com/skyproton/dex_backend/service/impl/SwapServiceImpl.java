package com.skyproton.dex_backend.service.impl;

import com.skyproton.dex_backend.service.SwapService;
import com.skyproton.dex_backend.tools.Blockchain;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

@Service
public class SwapServiceImpl implements SwapService {

    private final Blockchain blockchain;

    public SwapServiceImpl(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public static class ExactInputSingleParams extends DynamicStruct {
        public Address tokenIn;
        public Address tokenOut;
        public Uint24 fee;
        public Address recipient;
        public Uint256 deadline;
        public Uint256 amountIn;
        public Uint256 amountOutMinimum;
        public Uint160 sqrtPriceLimitX96;

        public ExactInputSingleParams(String tokenIn, String tokenOut, int fee, String recipient,
                                      BigInteger deadline, BigInteger amountIn, BigInteger amountOutMin,
                                      BigInteger sqrtPriceLimitX96) {
            super(new Address(tokenIn), new Address(tokenOut), new Uint24(fee), new Address(recipient),
                    new Uint256(deadline), new Uint256(amountIn), new Uint256(amountOutMin), new Uint160(sqrtPriceLimitX96));
        }
    }

    public String buildApproveTx(
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
    ) throws Exception {
        Web3j web3j = blockchain.getWeb3Provider(chainId);

        Function function = new Function(
                "approve",
                Arrays.asList(new Address(spender), new Uint256(amount)),
                Collections.singletonList(new TypeReference<Bool>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);
        EthGetTransactionCount txCount = web3j.ethGetTransactionCount(ownerAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = txCount.getTransactionCount();

        RawTransaction rawTx;
        if (eip1559) {
            rawTx = RawTransaction.createTransaction(
                    chainId,                      // long chainId
                    nonce,                        // BigInteger nonce
                    gasLimit,                     // BigInteger gasLimit
                    tokenAddress,                 // String to
                    BigInteger.ZERO,              // BigInteger value
                    encodedFunction,              // String data
                    maxPriorityFeePerGas,         // BigInteger maxPriorityFeePerGas
                    maxFeePerGas                  // BigInteger maxFeePerGas
            );
        } else {
            rawTx = RawTransaction.createTransaction(
                    nonce,                        // BigInteger nonce
                    gasPrice,                     // BigInteger gasPrice
                    gasLimit,                     // BigInteger gasLimit
                    tokenAddress,                 // String to
                    BigInteger.ZERO,              // BigInteger value
                    encodedFunction               // String data
            );
        }

        return Numeric.toHexString(TransactionEncoder.encode(rawTx, chainId));
    }

    @Override
    public String buildUniswapV3SwapExactInputSingleTx(
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
    ) throws Exception {
        Web3j web3j = blockchain.getWeb3Provider(chainId);

        String UNISWAP_V3_ROUTER = "0xE592427A0AEce92De3Edee1F18E0157C05861564";

        BigInteger deadline = BigInteger.valueOf(Instant.now().getEpochSecond() + 1800); // 30 min
        BigInteger sqrtPriceLimitX96 = BigInteger.ZERO; // No limit

        ExactInputSingleParams params = new ExactInputSingleParams(
                tokenIn, tokenOut, fee, walletAddress, deadline, amountIn, amountOutMin, sqrtPriceLimitX96
        );

        Function swapFunction = new Function(
                "exactInputSingle",
                Collections.singletonList(params),
                Collections.singletonList(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(swapFunction);

        EthGetTransactionCount txCount = web3j.ethGetTransactionCount(walletAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = txCount.getTransactionCount();

        RawTransaction rawTx;
        if (eip1559) {
            rawTx = RawTransaction.createTransaction(
                    chainId,                      // long chainId
                    nonce,                        // BigInteger nonce
                    gasLimit,                     // BigInteger gasLimit
                    UNISWAP_V3_ROUTER,            // String to
                    BigInteger.ZERO,              // BigInteger value
                    encodedFunction,              // String data
                    maxPriorityFeePerGas,         // BigInteger maxPriorityFeePerGas
                    maxFeePerGas                  // BigInteger maxFeePerGas
            );
        } else {
            rawTx = RawTransaction.createTransaction(
                    nonce,                        // BigInteger nonce
                    gasPrice,                     // BigInteger gasPrice
                    gasLimit,                     // BigInteger gasLimit
                    UNISWAP_V3_ROUTER,            // String to
                    BigInteger.ZERO,              // BigInteger value
                    encodedFunction               // String data
            );
        }

        return Numeric.toHexString(TransactionEncoder.encode(rawTx, chainId));
    }

    @Override
    public String sendSignedTransaction(String signedTx, int chainId) throws Exception {
        Web3j web3j = blockchain.getWeb3Provider(chainId);
        EthSendTransaction result = web3j.ethSendRawTransaction(signedTx).send();
        if (result.hasError()) {
            throw new RuntimeException("Transaction failed: " + result.getError().getMessage());
        }
        return result.getTransactionHash();
    }
}
