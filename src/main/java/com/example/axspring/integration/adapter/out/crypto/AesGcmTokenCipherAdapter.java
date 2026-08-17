package com.example.axspring.integration.adapter.out.crypto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.example.axspring.integration.application.port.out.TokenCipher;

@Component
public class AesGcmTokenCipherAdapter implements TokenCipher {
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    private static final int IV_LENGTH = 12;

    private static final int TAG_LENGTH_BITS = 128;

    private static final String VERSION = "v1";

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom;

    public AesGcmTokenCipherAdapter(
            TokenCryptoProperties properties) {
        byte[] keyBytes = Base64.getDecoder()
                .decode(properties.key());

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "Integration token encryption key must be 32 bytes");
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");

        this.secureRandom = new SecureRandom();
    }

    @Override
    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    parameterSpec);

            byte[] ciphertext = cipher.doFinal(
                    plaintext.getBytes(
                            StandardCharsets.UTF_8));

            ByteBuffer buffer = ByteBuffer.allocate(
                    iv.length
                            + ciphertext.length);

            buffer.put(iv);
            buffer.put(ciphertext);

            String encoded = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(
                            buffer.array());

            return VERSION + ":" + encoded;

        } catch (Exception e) {
            throw new TokenEncryptionException(
                    "Failed to encrypt integration token",
                    e);
        }
    }
        
    @Override
    public String decrypt(String encrypted) {
        if (encrypted == null) {
            return null;
        }

        try {
            String[] parts =
                    encrypted.split(":", 2);

            if (
                    parts.length != 2
                    || !VERSION.equals(parts[0])
            ) {
                throw new IllegalArgumentException(
                        "Unsupported encrypted token format"
                );
            }

            byte[] decoded =
                    Base64.getUrlDecoder()
                            .decode(parts[1]);

            ByteBuffer buffer =
                    ByteBuffer.wrap(decoded);

            byte[] iv =
                    new byte[IV_LENGTH];

            buffer.get(iv);

            byte[] ciphertext =
                    new byte[buffer.remaining()];

            buffer.get(ciphertext);


            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            GCMParameterSpec parameterSpec =
                    new GCMParameterSpec(
                            TAG_LENGTH_BITS,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    parameterSpec
            );

            byte[] plaintext =
                    cipher.doFinal(ciphertext);

            return new String(
                    plaintext,
                    StandardCharsets.UTF_8
            );
        } catch (Exception e) {
            throw new TokenEncryptionException(
                    "Failed to decrypt integration token",
                    e
            );
        }
    }
}

