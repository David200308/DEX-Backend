package com.skyproton.dex_backend.service.impl;

import com.skyproton.dex_backend.service.SwapService;
import com.skyproton.dex_backend.tools.Blockchain;
import com.skyproton.dex_backend.tools.Token;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final Token token;

    private final BigInteger GAS_LIMIT = BigInteger.valueOf(800000);
    private final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L); // 30 Gwei

    public SwapServiceImpl(Blockchain blockchain, Token token) {
        this.blockchain = blockchain;
        this.token = token;
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

    @Override
    public String uniswapSwapExactInputSingle(
            int chainId,
            String privateKey,
            String tokenIn,
            String tokenOut,
            int fee,
            BigInteger amountIn,
            BigInteger amountOutMin
    ) throws Exception {
        Web3j web3j = blockchain.getWeb3Provider(chainId);
        Credentials credentials = Credentials.create(privateKey);
        String walletAddress = credentials.getAddress();

        // UniSwap V3 Swap Router ETH Mainnet
        String UNISWAP_V3_ROUTER = "0xE592427A0AEce92De3Edee1F18E0157C05861564";

        // Approve token for swap router
        String approveTxHash = token.approveToken(tokenIn, walletAddress, UNISWAP_V3_ROUTER, amountIn, web3j, credentials);
        System.out.println("Approve tx hash: " + approveTxHash);

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

        RawTransaction rawTx = RawTransaction.createTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, UNISWAP_V3_ROUTER, BigInteger.ZERO, encodedFunction
        );

        byte[] signedTx = TransactionEncoder.signMessage(rawTx, chainId, credentials);
        String hexTx = Numeric.toHexString(signedTx);

        EthSendTransaction result = web3j.ethSendRawTransaction(hexTx).send();
        if (result.hasError()) throw new RuntimeException("Swap failed: " + result.getError().getMessage());

        return result.getTransactionHash();
    }

    @Override
    public String curveSwapExactInputSingle(
            int chainId,
            String privateKey,
            String tokenIn,
            String tokenOut,
            int fee,
            BigInteger amountIn,
            BigInteger amountOutMin
    ) throws Exception {
        return "";
    }
}
