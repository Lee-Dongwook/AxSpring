package com.example.axspring.auth.application.port.out;

import com.example.axspring.auth.domain.SessionId;
import com.example.axspring.user.domain.UserId;
import com.example.axspring.user.domain.UserRole;

public interface TokenIssuer {

    String issueAccessToken(
            UserId userId,
            SessionId sessionId,
            UserRole role
    );
}
