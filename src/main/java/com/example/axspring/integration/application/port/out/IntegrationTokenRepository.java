package com.example.axspring.integration.application.port.out;

import java.util.Optional;

import com.example.axspring.integration.domain.IntegrationProvider;
import com.example.axspring.integration.domain.IntegrationToken;
import com.example.axspring.user.domain.UserId;

public interface IntegrationTokenRepository {
    
    Optional<IntegrationToken> findByUserIdAndProvider(
        UserId userId,
        IntegrationProvider provider
    );

    IntegrationToken save(
        IntegrationToken token
    );
}
