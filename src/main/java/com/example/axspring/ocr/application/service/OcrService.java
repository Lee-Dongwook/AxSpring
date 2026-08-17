package com.example.axspring.ocr.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.axspring.automation.application.port.out.AutomationRunRepository;
import com.example.axspring.automation.domain.AutomationRun;
import com.example.axspring.ocr.application.port.in.OcrCommand;
import com.example.axspring.ocr.application.port.in.ParseBusinessCardUseCase;
import com.example.axspring.ocr.application.port.in.ParseReceiptUseCase;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

@Service
public class OcrService implements ParseBusinessCardUseCase, ParseReceiptUseCase {
    private final OcrProvider ocrProvider;
    private final AutomationRunRepository automationRunRepository;

    public OcrService(
        OcrProvider ocrProvider,
        AutomationRunRepository automationRunRepository
    ) {
        this.ocrProvider = ocrProvider;
        this.automationRunRepository = automationRunRepository;
    }

    @Override
    public BusinessCardOcrResult parseBusinessCard(
        OcrImage image
    ) {
        return ocrProvider.parseBusinessCard(image);
    }

    @Override
    public ReceiptOcrResult parseReceipt(
        OcrCommand command
    ) {
        Instant startedAt = Instant.now();

        AutomationRun run = AutomationRun.start(
            UUID.randomUUID().toString(),
            "ocr_receipt",
            Map.of(
                "fileName", command.image().fileName(),
                "fileSize", command.image().size(),
                "mimeType", command.image().mimeType()
            ),
            command.requestedBy(),
            startedAt
        );

        automationRunRepository.save(run);

        try {
            ReceiptOcrResult result = ocrProvider.parseReceipt(command.image());

            long durationMs =
                    Duration.between(
                            startedAt,
                            Instant.now()
                    ).toMillis();
            run.succeed(
                receiptOutput(result),
                durationMs,
                Instant.now()
            );

            automationRunRepository.save(run);
            return result;

        } catch (Exception e) {
            long durationMs = Duration.between(
                    startedAt,
                    Instant.now()
            ).toMillis();

            run.fail(
                e.getMessage(),
                durationMs,
                Instant.now()
            );

            automationRunRepository.save(run);
            throw e;
        }
    }

    private Map<String, Object> receiptOutput(ReceiptOcrResult result) {
        Map<String, Object> output = new LinkedHashMap<>();
        output.put("merchantName", result.merchantName());
        output.put("totalAmount", result.totalAmount());
        output.put("confidence", result.confidence());
        output.put("warningCount", result.warnings().size());
        return output;
    }
}
