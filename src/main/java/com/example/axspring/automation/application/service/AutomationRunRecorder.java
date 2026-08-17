package com.example.axspring.automation.application.service;


import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import com.example.axspring.automation.application.port.out.AutomationRunRepository;
import com.example.axspring.automation.domain.AutomationRun;
import com.example.axspring.user.domain.UserId;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AutomationRunRecorder {
    private final AutomationRunRepository automationRunRepository;
    
    public AutomationRunRecorder(
        AutomationRunRepository automationRunRepository
    ) {
        this.automationRunRepository = automationRunRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AutomationRun start(
        String runId,
        String type,
        Map<String, Object> input,
        UserId requestedById,
        Instant now
    ) {
        AutomationRun run = AutomationRun.start(
                runId,
                type,
                input,
                requestedById,
                now);

        return automationRunRepository.save(run);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(
            AutomationRun run,
            String errorMessage,
            long durationMs,
            Instant now
    ) {
        run.fail(
                errorMessage,
                durationMs,
                now
        );

        automationRunRepository.save(run);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void success(
            AutomationRun run,
            Map<String, Object> output,
            long durationMs,
            Instant now
    ) {
        run.succeed(output, durationMs, now);
        automationRunRepository.save(run);
    }
}
