package com.example.axspring.ocr.adapter.out.provider.claude;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.anthropic.client.AnthropicClient;
import com.example.axspring.ocr.application.exception.OcrProviderException;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

@Component
@ConditionalOnProperty(
        name = "app.ocr.provider",
        havingValue = "claude"
)
public class ClaudeVisionOcrAdapter implements OcrProvider {
    
    private final AnthropicClient client;
    private final ClaudeOcrProperties properties;

    public ClaudeVisionOcrAdapter(
          AnthropicClient client,
          ClaudeOcrProperties properties
    ) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public BusinessCardOcrResult parseBusinessCard(
            OcrImage image
    ) {
        throw notImplemented();
    }


    @Override
    public ReceiptOcrResult parseReceipt(
            OcrImage image
    ) {
        throw notImplemented();
    }

    private OcrProviderException notImplemented() {
        return new OcrProviderException(
                "Claude OCR provider is configured but not implemented yet",
                null);
    }
}
