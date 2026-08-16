package com.example.axspring.ocr.application.port.in;

import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;

public interface ParseBusinessCardUseCase {
    BusinessCardOcrResult parseBusinessCard(
        OcrImage image
    );
}
