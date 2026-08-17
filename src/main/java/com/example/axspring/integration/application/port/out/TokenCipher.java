package com.example.axspring.integration.application.port.out;

public interface TokenCipher {

    String encrypt(String plaintext);
    String decrypt(String ciphertext);
}
