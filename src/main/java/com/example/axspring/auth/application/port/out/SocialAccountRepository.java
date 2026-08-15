package com.example.axspring.auth.application.port.out;

import java.util.Optional;

import com.example.axspring.auth.domain.AuthProvider;
import com.example.axspring.auth.domain.SocialAccount;
import com.example.axspring.user.domain.UserId;

public interface SocialAccountRepository {
    Optional <SocialAccount> findByProviderAndProviderUserId(
        AuthProvider provider,
        String providerUserId
    );

    Optional<SocialAccount> findByUserIdAndProvider(
        UserId userId,
        AuthProvider provider
    );

    SocialAccount save(SocialAccount socialAccount);
}
