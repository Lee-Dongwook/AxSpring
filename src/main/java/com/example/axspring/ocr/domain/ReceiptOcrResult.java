package com.example.axspring.ocr.domain;

import java.time.LocalDate;
import java.util.List;

public record ReceiptOcrResult(
    String merchantName,
    LocalDate transactionDate,
    Long totalAmount,
    String currency,
    double confidence,
    List<String> warnings
) {
    public ReceiptOcrResult {
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }    
}
