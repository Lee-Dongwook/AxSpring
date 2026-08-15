package com.example.axspring.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailTest {
    
    @Test
    void normalize_lower_case_email() {
        Email email = new Email("TEST@Example.COM");

        assertThat(email.value())
                .isEqualTo("test@example.com");
    }

    @Test
    void email_should_not_be_empty() {
        assertThatThrownBy(() -> new Email(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void email_should_not_be_null() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
