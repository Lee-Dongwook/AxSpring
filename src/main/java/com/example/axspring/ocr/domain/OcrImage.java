package com.example.axspring.ocr.domain;

public record OcrImage(
    byte[] content,
    String fileName,
    String mimeType,
    long size
) {
    
    public OcrImage {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException(
                    "OCR image content must not be empty");
        }

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "OCR image fileName must not be blank"
            );
        }

        if (mimeType == null || mimeType.isBlank()) {
            throw new IllegalArgumentException(
                    "OCR image mimeType must not be blank"
            );
        }
    }
    
}
