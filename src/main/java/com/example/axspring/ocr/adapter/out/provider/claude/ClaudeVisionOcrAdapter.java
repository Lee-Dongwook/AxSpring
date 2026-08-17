package com.example.axspring.ocr.adapter.out.provider.claude;

import java.util.Base64;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.anthropic.client.AnthropicClient;
import com.example.axspring.ocr.adapter.out.provider.claude.dto.ClaudeBusinessCardResult;
import com.example.axspring.ocr.adapter.out.provider.claude.dto.ClaudeReceiptResult;
import com.example.axspring.ocr.application.exception.OcrProviderException;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(
        name = "app.ocr.provider",
        havingValue = "claude"
)
public class ClaudeVisionOcrAdapter implements OcrProvider {
    
    private final AnthropicClient client;
    private final ClaudeOcrProperties properties;
    private final ObjectMapper objectMapper;
    private final ClaudeOcrExecutor executor;

    public ClaudeVisionOcrAdapter(
          AnthropicClient client,
          ClaudeOcrProperties properties,
          ObjectMapper objectMapper,
          ClaudeOcrExecutor executor
    ) {
        this.client = client;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.executor = executor;
    }

    @Override
    public BusinessCardOcrResult parseBusinessCard(
            OcrImage image
    ) {
        return executor.execute(() -> {
            String json = request(
                image,
                businessCardPrompt()
            );

            try {
                ClaudeBusinessCardResult parsed = 
                    objectMapper.readValue(
                        json,
                        ClaudeBusinessCardResult.class
                    );
                
                return new BusinessCardOcrResult(
                        parsed.name(),
                        parsed.company(),
                        parsed.department(),
                        parsed.position(),
                        parsed.email(),
                        parsed.phone(),
                        parsed.confidence()
                );

            } catch(Exception e) {
                throw new OcrProviderException(
                        "Failed to parse OCR response",
                        e
                );
            }
        });
    }


    @Override
    public ReceiptOcrResult parseReceipt(
            OcrImage image
    ) {
        return executor.execute(() -> {
            String json = request(
                    image,
                    receiptPrompt());

            try {
                ClaudeReceiptResult parsed = objectMapper.readValue(
                        json,
                        ClaudeReceiptResult.class);

                return new ReceiptOcrResult(
                        parsed.merchantName(),
                        parsed.transactionDate(),
                        parsed.totalAmount(),
                        parsed.currency(),
                        parsed.confidence(),
                        parsed.warnings());

            } catch (Exception e) {
                throw new OcrProviderException(
                        "Failed to parse OCR response",
                        e);
            }
        });
    }
    
    private String request(
        OcrImage image,
        String prompt
    ) {
        try {
            String base64 = Base64.getEncoder()
                    .encodeToString(image.content());

            return callClaude(base64, image.mimeType(), prompt);

        } catch (OcrProviderException e) {
            throw e;
        } catch (Exception e) {
            throw new OcrProviderException("Claude OCR request failed", e);
        }
    }
    
    private String callClaude(
         String base64,
         String mimeType,
         String prompt
    ) {
        throw new UnsupportedOperationException(
                "Wire Anthropic Messages API here");
    }
    
      private String businessCardPrompt() {
        return """
                Analyze this business card.

                Return ONLY valid JSON with this shape:
                {
                  "name": string | null,
                  "company": string | null,
                  "department": string | null,
                  "position": string | null,
                  "email": string | null,
                  "phone": string | null,
                  "confidence": number
                }

                confidence must be between 0 and 1.
                Do not invent missing values.
                """;
    }

      private String receiptPrompt() {
        return """
                Analyze this receipt.

                Return ONLY valid JSON with this shape:
                {
                  "merchantName": string | null,
                  "transactionDate": "YYYY-MM-DD" | null,
                  "totalAmount": number | null,
                  "currency": string | null,
                  "confidence": number,
                  "warnings": string[]
                }

                totalAmount must be the final paid amount.
                Do not invent missing values.
                """;
    }
}
