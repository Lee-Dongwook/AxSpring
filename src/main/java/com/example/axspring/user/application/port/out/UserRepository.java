package com.example.axspring.user.application.port.out;

import java.util.Optional;

import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;

public interface UserRepository {
    boolean existsByEmail(Email email);

    Optional<User> findByEmail(Email email);

    User save(User user);
}
