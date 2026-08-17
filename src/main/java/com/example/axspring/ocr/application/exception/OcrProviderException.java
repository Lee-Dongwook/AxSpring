package com.example.axspring.ocr.application.exception;

public class OcrProviderException
        extends RuntimeException {

    public OcrProviderException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
