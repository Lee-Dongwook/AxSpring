package com.example.axspring.ocr.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.axspring.automation.application.service.AutomationRunRecorder;
import com.example.axspring.automation.domain.AutomationRun;
import com.example.axspring.ocr.application.port.in.OcrCommand;
import com.example.axspring.ocr.application.port.in.ParseBusinessCardUseCase;
import com.example.axspring.ocr.application.port.in.ParseReceiptUseCase;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

@Service
public class OcrService implements ParseBusinessCardUseCase, ParseReceiptUseCase {
    private final OcrProvider ocrProvider;
    private final AutomationRunRecorder automationRunRecorder;

    public OcrService(
        OcrProvider ocrProvider,
        AutomationRunRecorder automationRunRecorder
    ) {
        this.ocrProvider = ocrProvider;
        this.automationRunRecorder = automationRunRecorder;
    }

    @Override
    public BusinessCardOcrResult parseBusinessCard(
        OcrCommand command
    ) {
        return recordRun(
                command,
                "ocr_business_card",
                () -> ocrProvider.parseBusinessCard(command.image()),
                this::businessCardOutput
        );
    }

    @Override
    public ReceiptOcrResult parseReceipt(
        OcrCommand command
    ) {
        return recordRun(
                command,
                "ocr_receipt",
                () -> ocrProvider.parseReceipt(command.image()),
                this::receiptOutput
        );
    }

    private <T> T recordRun(
            OcrCommand command,
            String type,
            OcrOperation<T> operation,
            OcrOutput<T> outputMapper
    ) {
        Instant startedAt = Instant.now();
        AutomationRun run = automationRunRecorder.start(
                UUID.randomUUID().toString(),
                type,
                input(command),
                command.requestedBy(),
                startedAt);

        try {
            T result = operation.execute();
            Instant finishedAt = Instant.now();
            long durationMs = Duration.between(startedAt, finishedAt).toMillis();
            automationRunRecorder.success(
                    run,
                    outputMapper.map(result),
                    durationMs,
                    finishedAt);
            return result;
        } catch (Exception e) {
            Instant failedAt = Instant.now();
            long durationMs = Duration.between(startedAt, failedAt).toMillis();

            automationRunRecorder.fail(
                    run,
                    safeErrorMessage(e),
                    durationMs,
                    failedAt);
            throw e;
        }
    }

    private Map<String, Object> input(OcrCommand command) {
        return Map.of(
                "fileName", command.image().fileName(),
                "fileSize", command.image().size(),
                "mimeType", command.image().mimeType());
    }

    private Map<String, Object> receiptOutput(ReceiptOcrResult result) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("merchantName", result.merchantName());
        output.put("totalAmount", result.totalAmount());
        output.put("confidence", result.confidence());
        output.put("warningCount", result.warnings().size());
        return output;
    }

    private Map<String, Object> businessCardOutput(BusinessCardOcrResult result) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("name", result.name());
        output.put("company", result.company());
        output.put("email", result.email());
        output.put("phone", result.phone());
        output.put("confidence", result.confidence());
        return output;
    }

    @FunctionalInterface
    private interface OcrOperation<T> {
        T execute();
    }

    @FunctionalInterface
    private interface OcrOutput<T> {
        Map<String, Object> map(T result);
    }
    
    private String safeErrorMessage(Exception e) {
        String message = e.getMessage();

        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }

        return message.length() > 1000
                ? message.substring(0, 1000)
                : message;
    }
}
