package com.example.axspring.ocr.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.axspring.automation.application.port.out.AutomationRunRepository;
import com.example.axspring.automation.domain.AutomationRun;
import com.example.axspring.automation.domain.AutomationStatus;
import com.example.axspring.ocr.application.port.in.OcrCommand;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;
import com.example.axspring.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class OcrServiceTest {
    @Mock
    OcrProvider ocrProvider;

    @Mock
    AutomationRunRepository automationRunRepository;

    OcrService ocrService;

    @BeforeEach
    void setUp() {
        ocrService = new OcrService(ocrProvider, automationRunRepository);
    }

    @Test
    void parseReceipt_records_successful_automation_run() {
        OcrCommand command = command();
        ReceiptOcrResult expected = new ReceiptOcrResult(
                "상점", LocalDate.of(2026, 8, 17), 12000L, "KRW", 0.9, List.of("확인 필요"));
        when(ocrProvider.parseReceipt(command.image())).thenReturn(expected);

        ReceiptOcrResult actual = ocrService.parseReceipt(command);

        assertThat(actual).isEqualTo(expected);
        ArgumentCaptor<AutomationRun> captor = ArgumentCaptor.forClass(AutomationRun.class);
        verify(automationRunRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        AutomationRun completedRun = captor.getAllValues().get(1);
        assertThat(completedRun.type()).isEqualTo("ocr_receipt");
        assertThat(completedRun.status()).isEqualTo(AutomationStatus.SUCCESS);
        assertThat(completedRun.output()).containsEntry("totalAmount", 12000L)
                .containsEntry("warningCount", 1);
    }

    @Test
    void parseReceipt_records_failed_automation_run_and_rethrows_error() {
        OcrCommand command = command();
        when(ocrProvider.parseReceipt(command.image())).thenThrow(new IllegalStateException("OCR unavailable"));

        assertThatThrownBy(() -> ocrService.parseReceipt(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("OCR unavailable");

        ArgumentCaptor<AutomationRun> captor = ArgumentCaptor.forClass(AutomationRun.class);
        verify(automationRunRepository, org.mockito.Mockito.times(2)).save(captor.capture());
        AutomationRun failedRun = captor.getAllValues().get(1);
        assertThat(failedRun.status()).isEqualTo(AutomationStatus.FAILED);
        assertThat(failedRun.errorMessage()).isEqualTo("OCR unavailable");
    }

    private OcrCommand command() {
        return new OcrCommand(
                new UserId("user-1"),
                new OcrImage(new byte[] {1}, "receipt.png", "image/png", 1L)
        );
    }
}
