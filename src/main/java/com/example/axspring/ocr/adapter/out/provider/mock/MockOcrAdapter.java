package com.example.axspring.ocr.adapter.out.provider.mock;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

@Component
@ConditionalOnProperty(
    name = "app.ocr.provider",
    havingValue = "mock",
    matchIfMissing = true
)
public class MockOcrAdapter implements OcrProvider {
    @Override
    public BusinessCardOcrResult parseBusinessCard(
        OcrImage image
    ) {
        return new BusinessCardOcrResult(
                "홍길동",
                "AxSpring",
                "Backend",
                "Engineer",
                "hong@example.com",
                "010-1234-5678",
                0.95);
    }
    
    @Override
    public ReceiptOcrResult parseReceipt(
        OcrImage image
    ) {
        return new ReceiptOcrResult(
                "스타벅스",
                LocalDate.now(),
                12500L,
                "KRW",
                0.92,
                List.of());
    }
}
