package com.example.axspring.auth.adapter.out.persistence;

import com.example.axspring.auth.domain.SocialAccount;
import com.example.axspring.user.domain.UserId;

public final class SocialAccountPersistenceMapper {

    private SocialAccountPersistenceMapper() {
    }

    public static SocialAccountJpaEntity toEntity(
            SocialAccount socialAccount
    ) {
        return new SocialAccountJpaEntity(
                socialAccount.userId().value(),
                socialAccount.provider(),
                socialAccount.providerUserId(),
                socialAccount.email(),
                socialAccount.createdAt(),
                socialAccount.updatedAt()
        );
    }

    public static SocialAccount toDomain(
            SocialAccountJpaEntity entity
    ) {
        return SocialAccount.restore(
                new UserId(entity.getUserId()),
                entity.getProvider(),
                entity.getProviderUserId(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
