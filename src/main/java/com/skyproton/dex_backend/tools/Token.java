package com.skyproton.dex_backend.tools;

import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

@Component
public class Token {
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(800000);
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L); // 30 Gwei

    public String approveToken(
            String tokenAddress,
            String ownerAddress,
            String spender,
            BigInteger amount,
            Web3j web3j,
            Credentials credentials
    ) throws Exception {
        Function function = new Function(
                "approve",
                Arrays.asList(new Address(spender), new Uint256(amount)),
                Collections.singletonList(new TypeReference<Bool>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);
        EthGetTransactionCount txCount = web3j.ethGetTransactionCount(ownerAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = txCount.getTransactionCount();

        RawTransaction rawTx = RawTransaction.createTransaction(
                nonce, GAS_PRICE, GAS_LIMIT, tokenAddress, BigInteger.ZERO, encodedFunction
        );

        byte[] signedTx = TransactionEncoder.signMessage(rawTx, credentials);
        String hexTx = Numeric.toHexString(signedTx);

        EthSendTransaction result = web3j.ethSendRawTransaction(hexTx).send();
        if (result.hasError()) throw new RuntimeException("Approve failed: " + result.getError().getMessage());

        return result.getTransactionHash();
    }
}
