package com.example.axspring.ocr.application.service;

import org.springframework.stereotype.Service;

import com.example.axspring.ocr.application.port.in.ParseBusinessCardUseCase;
import com.example.axspring.ocr.application.port.in.ParseReceiptUseCase;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

@Service
public class OcrService implements ParseBusinessCardUseCase, ParseReceiptUseCase {
    private final OcrProvider ocrProvider;

    public OcrService(
        OcrProvider ocrProvider
    ) {
        this.ocrProvider = ocrProvider;
    }

    @Override
    public BusinessCardOcrResult parseBusinessCard(
        OcrImage image
    ) {
        return ocrProvider.parseBusinessCard(image);
    }

    @Override
    public ReceiptOcrResult parseReceipt(
        OcrImage image
    ) {
        return ocrProvider.parseReceipt(image);
    }
}
