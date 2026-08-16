package com.example.axspring.ocr.application.exception;

public class UnsupportedOcrFileTypeException
        extends RuntimeException {

    public UnsupportedOcrFileTypeException(
            String mimeType
    ) {
        super(
                "Unsupported OCR file type: "
                        + mimeType
        );
    }
}
