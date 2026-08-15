package com.example.axspring.auth.application.port.out;

import com.example.axspring.auth.domain.UserCredential;

public interface UserCredentialRepository {

    UserCredential save(UserCredential credential);
}
