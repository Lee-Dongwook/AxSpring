package com.example.axspring.ocr.adapter.out.provider.claude.dto;

import java.time.LocalDate;
import java.util.List;

public record ClaudeReceiptResult(
        String merchantName,
        LocalDate transactionDate,
        Long totalAmount,
        String currency,
        double confidence,
        List<String> warnings
) {
}
