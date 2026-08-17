package com.example.axspring.automation.adapter.out.persistence;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.example.axspring.automation.application.port.out.AutomationRunRepository;
import com.example.axspring.automation.domain.AutomationRun;

@Repository
@Profile("!in-memory")
public class JpaAutomationRunRepositoryAdapter implements AutomationRunRepository {
    private final SpringDataAutomationRunRepository repository;

    public JpaAutomationRunRepositoryAdapter(
            SpringDataAutomationRunRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public AutomationRun save(AutomationRun run) {
        repository.save(toEntity(run));
        return run;
    }

    private AutomationRunJpaEntity toEntity(AutomationRun run) {
        return new AutomationRunJpaEntity(
                run.id(),
                run.type(),
                run.status(),
                run.input(),
                run.output(),
                run.errorMessage(),
                run.requestedById().value(),
                null,
                run.durationMs(),
                run.createdAt(),
                run.updatedAt()
        );
    }
}
