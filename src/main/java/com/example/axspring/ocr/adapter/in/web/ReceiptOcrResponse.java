package com.example.axspring.ocr.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import com.example.axspring.ocr.domain.ReceiptOcrResult;

public record ReceiptOcrResponse(
        String merchantName,
        LocalDate transactionDate,
        Long totalAmount,
        String currency,
        double confidence,
        List<String> warnings
) {

    public static ReceiptOcrResponse from(
            ReceiptOcrResult result
    ) {
        return new ReceiptOcrResponse(
                result.merchantName(),
                result.transactionDate(),
                result.totalAmount(),
                result.currency(),
                result.confidence(),
                result.warnings()
        );
    }
}
