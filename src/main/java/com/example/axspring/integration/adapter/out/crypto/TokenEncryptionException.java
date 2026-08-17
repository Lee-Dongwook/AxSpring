package com.example.axspring.integration.adapter.out.crypto;

public class TokenEncryptionException
        extends RuntimeException {

    public TokenEncryptionException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
