package com.example.axspring.user.adapter.out.persistence;

import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;
import com.example.axspring.user.domain.UserId;

public final class UserPersistenceMapper {
    private UserPersistenceMapper() {
    }

    public static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id().value(),
                user.name(),
                user.email().value(),
                user.imageUrl(),
                user.role(),
                user.department(),
                user.position(),
                user.hireDate(),
                user.birthDate(),
                user.active(),
                user.createdAt(),
                user.updatedAt()
        );
    }

    public static User toDomain(UserJpaEntity entity) {
         return User.restore(
                new UserId(entity.getId()),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getImageUrl(),
                entity.getRole(),
                entity.getDepartment(),
                entity.getPosition(),
                entity.getHireDate(),
                entity.getBirthDate(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
