package com.example.axspring.ocr.application.port.in;

import com.example.axspring.ocr.domain.BusinessCardOcrResult;

public interface ParseBusinessCardUseCase {
    BusinessCardOcrResult parseBusinessCard(
        OcrCommand command
    );
}
