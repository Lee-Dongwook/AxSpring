package com.example.axspring.auth.application.port.out;

import java.util.Optional;

import com.example.axspring.auth.domain.UserCredential;
import com.example.axspring.user.domain.UserId;

public interface UserCredentialRepository {

    Optional<UserCredential> findByUserId(UserId userId);

    UserCredential save(UserCredential credential);
}
