package com.example.axspring.automation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAutomationRunRepository
        extends JpaRepository<AutomationRunJpaEntity, String> {
}
