package com.insightboard.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.insightboard.api.auth.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginWithValidAdmin() {
        var tokens = authService.login(new LoginRequest("admin", "admin"));
        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
    }

    @Test
    void loginRejectsInvalidPassword() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("admin", "bad")))
                .isInstanceOf(ResponseStatusException.class);
    }
}
