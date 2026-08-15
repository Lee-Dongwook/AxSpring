package com.example.axspring.user.application.port.out;

import com.example.axspring.user.domain.Email;
import com.example.axspring.user.domain.User;

public interface UserRepository {
    boolean existsByEmail(Email email);

    User save(User user);
}
