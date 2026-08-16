package com.example.axspring.ocr.application.exception;

public class OcrFileTooLargeException
        extends RuntimeException {

    public OcrFileTooLargeException(
            long maxBytes
    ) {
        super(
                "OCR file exceeds maximum size: "
                        + maxBytes
        );
    }
}
