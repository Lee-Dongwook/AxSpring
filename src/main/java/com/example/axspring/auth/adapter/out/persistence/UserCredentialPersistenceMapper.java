package com.example.axspring.auth.adapter.out.persistence;

import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.domain.UserId;

public final class UserCredentialPersistenceMapper {

    private UserCredentialPersistenceMapper() {
    }

    public static UserCredentialJpaEntity toEntity(
            UserCredential credential
    ) {
        return new UserCredentialJpaEntity(
                credential.userId().value(),
                credential.passwordHash(),
                credential.mustChangePassword(),
                credential.passwordChangedAt()
        );
    }

    public static UserCredential toDomain(
            UserCredentialJpaEntity entity
    ) {
        return UserCredential.restore(
                new UserId(entity.getUserId()),
                entity.getPasswordHash(),
                entity.isMustChangePassword(),
                entity.getPasswordChangedAt()
        );
    }
}
