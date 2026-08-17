package com.example.axspring.automation.application.port.out;

import com.example.axspring.automation.domain.AutomationRun;

public interface AutomationRunRepository {
    AutomationRun save(AutomationRun run);
}
