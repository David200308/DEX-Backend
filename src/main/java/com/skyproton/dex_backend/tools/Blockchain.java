package com.skyproton.dex_backend.tools;

import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.Map;

@Component
public class Blockchain {
    private final Config config;
    private final Map<Integer, String> WEB3_INFURA_URLS = Map.of(
            1, "https://mainnet.infura.io/v3/",
            137, "https://polygon-mainnet.infura.io/v3/",
            8453, "https://base-mainnet.infura.io/v3/",
            11155111, "https://sepolia.infura.io/v3/",
            80002, "https://polygon-amoy.infura.io/v3/"
    );

    public Blockchain(Config config) {
        this.config = config;
    }

    public boolean isWalletAddress(String address) {
        return address != null && address.matches("^(0x)?[0-9a-fA-F]{40}$");
    }

    public Web3j getWeb3Provider(int chainId) {
        String url = WEB3_INFURA_URLS.getOrDefault(chainId, "https://mainnet.infura.io/v3/");
        return Web3j.build(new HttpService(url + config.getInfuraApiKey()));
    }
}
