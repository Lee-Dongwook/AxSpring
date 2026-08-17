package com.example.axspring.ocr.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import com.example.axspring.automation.application.port.out.AutomationRunRepository;
import com.example.axspring.automation.application.service.AutomationRunRecorder;
import com.example.axspring.automation.domain.AutomationRun;
import com.example.axspring.automation.domain.AutomationStatus;
import com.example.axspring.ocr.adapter.out.provider.mock.MockOcrAdapter;
import com.example.axspring.ocr.application.port.out.OcrProvider;
import com.example.axspring.ocr.application.service.OcrService;
import com.example.axspring.ocr.domain.BusinessCardOcrResult;
import com.example.axspring.ocr.domain.OcrImage;
import com.example.axspring.ocr.domain.ReceiptOcrResult;

@WebMvcTest(OcrController.class)
@Import(OcrSmokeTest.SmokeConfig.class)
class OcrSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryAutomationRunRepository automationRunRepository;

    @BeforeEach
    void clearRuns() {
        automationRunRepository.clear();
    }

    @Test
    void mock_ocr_smoke_flow() throws Exception {
        mockMvc.perform(multipart("/api/ocr/receipt")
                        .file(image("receipt.png", "image/png", new byte[] {1})))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(multipart("/api/ocr/receipt")
                        .file(image("receipt.png", "image/png", new byte[] {1}))
                        .with(jwt().jwt(token -> token.subject("smoke-user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantName").value("스타벅스"))
                .andExpect(jsonPath("$.totalAmount").value(12500));

        assertThat(automationRunRepository.statuses())
                .containsExactly(AutomationStatus.RUNNING, AutomationStatus.SUCCESS);
        assertThat(automationRunRepository.latest().output())
                .containsEntry("totalAmount", 12500L);

        mockMvc.perform(multipart("/api/ocr/receipt")
                        .file(image("receipt.txt", "text/plain", new byte[] {1}))
                        .with(jwt()))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(multipart("/api/ocr/receipt")
                        .file(image("large.png", "image/png", new byte[5 * 1024 * 1024 + 1]))
                        .with(jwt()))
                .andExpect(status().is4xxClientError());

        automationRunRepository.clear();
        mockMvc.perform(multipart("/api/ocr/receipt")
                        .file(image("failure-mock.png", "image/png", new byte[] {1}))
                        .with(jwt()))
                .andExpect(status().is5xxServerError());

        assertThat(automationRunRepository.statuses())
                .containsExactly(AutomationStatus.RUNNING, AutomationStatus.FAILED);
    }

    private MockMultipartFile image(String name, String contentType, byte[] content) {
        return new MockMultipartFile("file", name, contentType, content);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class SmokeConfig {
        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
            return http.build();
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return token -> {
                throw new UnsupportedOperationException("JWT decoding is not used in this smoke test");
            };
        }

        @Bean
        InMemoryAutomationRunRepository automationRunRepository() {
            return new InMemoryAutomationRunRepository();
        }

        @Bean
        AutomationRunRecorder automationRunRecorder(InMemoryAutomationRunRepository repository) {
            return new AutomationRunRecorder(repository);
        }

        @Bean
        OcrProvider ocrProvider() {
            MockOcrAdapter mockOcrAdapter = new MockOcrAdapter();
            return new OcrProvider() {
                @Override
                public BusinessCardOcrResult parseBusinessCard(OcrImage image) {
                    return mockOcrAdapter.parseBusinessCard(image);
                }

                @Override
                public ReceiptOcrResult parseReceipt(OcrImage image) {
                    if (image.fileName().startsWith("failure-mock")) {
                        throw new IllegalStateException("failure-mock");
                    }
                    return mockOcrAdapter.parseReceipt(image);
                }
            };
        }

        @Bean
        OcrService ocrService(OcrProvider ocrProvider, AutomationRunRecorder recorder) {
            return new OcrService(ocrProvider, recorder);
        }
    }

    static class InMemoryAutomationRunRepository implements AutomationRunRepository {
        private final List<AutomationRun> savedRuns = new ArrayList<>();

        @Override
        public AutomationRun save(AutomationRun run) {
            savedRuns.add(AutomationRun.restore(
                    run.id(),
                    run.type(),
                    run.status(),
                    run.input(),
                    run.output(),
                    run.errorMessage(),
                    run.requestedById(),
                    run.durationMs(),
                    run.createdAt(),
                    run.updatedAt()));
            return run;
        }

        void clear() {
            savedRuns.clear();
        }

        List<AutomationStatus> statuses() {
            return savedRuns.stream().map(AutomationRun::status).toList();
        }

        AutomationRun latest() {
            return savedRuns.getLast();
        }
    }
}
