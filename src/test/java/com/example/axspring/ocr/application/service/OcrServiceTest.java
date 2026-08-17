package com.example.axspring.ocr.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.axspring.automation.application.service.AutomationRunRecorder;
import com.example.axspring.automation.domain.AutomationRun;
import com.example.axspring.audit.application.service.AuditRecorder;
import com.example.axspring.ocr.application.port.in.OcrCommand;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;
import com.example.axspring.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class OcrServiceTest {
    @Mock
    OcrProvider ocrProvider;

    @Mock
    AutomationRunRecorder automationRunRecorder;

    @Mock
    AuditRecorder auditRecorder;

    OcrService ocrService;

    @BeforeEach
    void setUp() {
        ocrService = new OcrService(ocrProvider, automationRunRecorder, auditRecorder);
    }

    @Test
    void parseReceipt_records_successful_automation_run() {
        OcrCommand command = command();
        ReceiptOcrResult expected = new ReceiptOcrResult(
                "상점", LocalDate.of(2026, 8, 17), 12000L, "KRW", 0.9, List.of("확인 필요"));
        when(ocrProvider.parseReceipt(command.image())).thenReturn(expected);
        when(automationRunRecorder.start(any(), eq("ocr_receipt"), any(), any(), any()))
                .thenReturn(run());

        ReceiptOcrResult actual = ocrService.parseReceipt(command);

        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<Map<String, Object>> outputCaptor = ArgumentCaptor.forClass(Map.class);
        verify(automationRunRecorder).success(any(), outputCaptor.capture(), any(Long.class), any());
        assertThat(outputCaptor.getValue()).containsEntry("totalAmount", 12000L)
                .containsEntry("warningCount", 1);
        verify(auditRecorder).record(
                eq(new UserId("user-1")),
                eq("OCR_RECEIPT_PARSED"),
                eq("automation_run"),
                eq("run-1"),
                eq(null),
                any(),
                eq(null),
                eq(null));
    }

    @Test
    void parseReceipt_records_failed_automation_run_and_rethrows_error() {
        OcrCommand command = command();
        when(ocrProvider.parseReceipt(command.image())).thenThrow(new IllegalStateException("OCR unavailable"));
        when(automationRunRecorder.start(any(), eq("ocr_receipt"), any(), any(), any()))
                .thenReturn(run());

        assertThatThrownBy(() -> ocrService.parseReceipt(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("OCR unavailable");

        verify(automationRunRecorder).fail(any(), eq("OCR unavailable"), any(Long.class), any());
    }

    @Test
    void parseBusinessCard_records_successful_automation_run() {
        OcrCommand command = command();
        BusinessCardOcrResult expected = new BusinessCardOcrResult(
                "홍길동", "AxSpring", "개발팀", "개발자", "hong@example.com", "010-1234-5678", 0.95);
        when(ocrProvider.parseBusinessCard(command.image())).thenReturn(expected);
        when(automationRunRecorder.start(any(), eq("ocr_business_card"), any(), any(), any()))
                .thenReturn(run());

        BusinessCardOcrResult actual = ocrService.parseBusinessCard(command);

        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<Map<String, Object>> outputCaptor = ArgumentCaptor.forClass(Map.class);
        verify(automationRunRecorder).success(any(), outputCaptor.capture(), any(Long.class), any());
        assertThat(outputCaptor.getValue()).containsEntry("name", "홍길동")
                .containsEntry("email", "hong@example.com");
    }

    private OcrCommand command() {
        return new OcrCommand(
                new UserId("user-1"),
                new OcrImage(new byte[] {1}, "receipt.png", "image/png", 1L),
                null,
                null
        );
    }

    private AutomationRun run() {
        return AutomationRun.start(
                "run-1", "test", Map.of(), new UserId("user-1"), Instant.now());
    }
}
