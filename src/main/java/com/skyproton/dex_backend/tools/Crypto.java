package com.skyproton.dex_backend.tools;

import com.password4j.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.jose4j.jwt.JwtClaims;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

@Component
public class Crypto {
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String INIT_VECTOR = "encryptionIntVec";
    private final Config config;

    @Autowired
    public Crypto(Config config) {
        this.config = config;
    }

    public String hashPassword(String data) {
        return Password.hash(data)
                .withBcrypt()
                .getResult();
    }

    public boolean validatePassword(String password, String hashedPassword) {
        return Password.check(password, hashedPassword)
                .withBcrypt();
    }

    public String encrypt(String data) {
        try {
            IvParameterSpec iv = new IvParameterSpec("encryptionIntVec".getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(
                    config.getKey().getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting: " + e.getMessage());
        }
    }

    public String decrypt(String encryptedData) {
        try {
            IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec secretKey = new SecretKeySpec(
                    config.getKey().getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting: " + e.getMessage());
        }
    }

    public String createJwt(
            String subject,
            String audience,
            String authUuid
    ) throws JoseException {
        final String JWT_SECRET_KEY = config.getJwtkey();

        JwtClaims claims = new JwtClaims();
        claims.setSubject(subject);
        claims.setAudience(audience);
        claims.setClaim("authUuid", authUuid);
        claims.setExpirationTimeMinutesInTheFuture(60);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jws.setKey(new HmacKey(JWT_SECRET_KEY.getBytes()));

        return jws.getCompactSerialization();
    }

    public JwtClaims verifyJwt(
            String subject,
            String token
    ) throws Exception {
        final String JWT_SECRET_KEY = config.getJwtkey();

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setExpectedSubject(subject)
                .setVerificationKey(new HmacKey(JWT_SECRET_KEY.getBytes()))
                .build();

        return jwtConsumer.processToClaims(token);
    }

    public boolean signatureVerification(
            String message,
            String signature,
            String walletAddress
    ) {
        try {
            if (message == null || signature == null || walletAddress == null) return false;

            String cleanAddress = walletAddress.startsWith("0x")
                    ? walletAddress.substring(2).toLowerCase()
                    : walletAddress.toLowerCase();

            String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
            byte[] prefixBytes = prefix.getBytes(StandardCharsets.UTF_8);
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            byte[] combined = new byte[prefixBytes.length + messageBytes.length];
            System.arraycopy(prefixBytes, 0, combined, 0, prefixBytes.length);
            System.arraycopy(messageBytes, 0, combined, prefixBytes.length, messageBytes.length);

            byte[] messageHash = Hash.sha3(combined);
            byte[] signatureBytes;

            try {
                signatureBytes = Numeric.hexStringToByteArray(signature);
            } catch (IllegalArgumentException e) {
                return false;
            }

            if (signatureBytes.length != 65) return false;
            byte v = signatureBytes[64];
            if (v < 27) v += 27;

            byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
            byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);

            ECDSASignature ecdsaSignature = new ECDSASignature(
                    new BigInteger(1, r),
                    new BigInteger(1, s)
            );

            BigInteger recoveredPublicKey;
            try {
                recoveredPublicKey = Sign.recoverFromSignature(v - 27, ecdsaSignature, messageHash);
            } catch (Exception e) {
                return false;
            }

            if (recoveredPublicKey == null) return false;

            String recoveredAddress = Keys.getAddress(recoveredPublicKey);
            return recoveredAddress.equalsIgnoreCase(cleanAddress);
        } catch (Exception e) {
            return false;
        }
    }

}
