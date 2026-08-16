package com.example.axspring.ocr.application.port.in;

import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

public interface ParseReceiptUseCase {
    ReceiptOcrResult parseReceipt(
        OcrImage image
    );
}
