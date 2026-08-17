package com.example.axspring.ocr.application.port.in;

import com.example.axspring.ocr.domain.ReceiptOcrResult;

public interface ParseReceiptUseCase {
    ReceiptOcrResult parseReceipt(
        OcrCommand command
    );
}
