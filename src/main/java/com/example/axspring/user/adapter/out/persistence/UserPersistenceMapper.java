package com.example.axspring.user.adapter.out.persistence;

import java.util.List;

import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;
import com.example.axspring.user.domain.UserId;

public final class UserPersistenceMapper {
    private UserPersistenceMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        List<String> emailAliases = user.emailAliases()
                .stream()
                .map(Email::value)
                .toList();

        return new UserJpaEntity(
                user.id().value(),
                user.name(),
                user.email().value(),
                user.emailVerifiedAt(),
                user.imageUrl(),
                user.passwordHash(),
                user.role(),
                user.department(),
                user.position(),
                user.hireDate(),
                user.birthDate(),
                user.slackUserId(),
                user.googleAccountId(),
                user.notionPersonId(),
                user.linearUserId(),
                user.githubLogin(),
                emailAliases,
                user.active(),
                user.mustChangePassword(),
                user.passwordChangedAt(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
         List<Email> emailAliases = entity.getEmailAliases()
                .stream()
                .map(Email::new)
                .toList();

         return User.restore(
                new UserId(entity.getId()),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getEmailVerifiedAt(),
                entity.getImageUrl(),
                entity.getPasswordHash(),
                entity.getRole(),
                entity.getDepartment(),
                entity.getPosition(),
                entity.getHireDate(),
                entity.getBirthDate(),
                entity.getSlackUserId(),
                entity.getGoogleAccountId(),
                entity.getNotionPersonId(),
                entity.getLinearUserId(),
                entity.getGithubLogin(),
                emailAliases,
                entity.isActive(),
                entity.isMustChangePassword(),
                entity.getPasswordChangedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
