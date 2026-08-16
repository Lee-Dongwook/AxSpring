package com.example.axspring.ocr.application.port.out;

import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

public interface OcrProvider {
    BusinessCardOcrResult parseBusinessCard(
        OcrImage image
    );

    ReceiptOcrResult parseReceipt(
        OcrImage image
    );
}
