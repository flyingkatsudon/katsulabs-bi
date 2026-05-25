package org.cboard.application.auth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.cboard.domain.user.UserAccount;
import org.cboard.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginUseCase")
class LoginUseCaseTest {

    private UserRepository userRepository;
    private PasswordHasher passwordHasher;
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordHasher = plain -> "HASHED";
        loginUseCase = new LoginUseCase(userRepository, passwordHasher, roleId -> List.of(1L));
    }

    @DisplayName("올바른 비밀번호면 로그인에 성공한다")
    @Test
    void success() {
        var account = new UserAccount(
                "admin01", "SH", "admin01", "Admin", "HASHED", "1", 0, "0", "N");
        when(userRepository.findByUserIdAndBusinessCode("admin01", "SH")).thenReturn(Optional.of(account));

        var user = loginUseCase.login(new LoginCommand("SH", "admin01", "admin123"));

        verify(userRepository).resetLoginFailure("admin01", "SH");
        org.assertj.core.api.Assertions.assertThat(user.userId()).isEqualTo("admin01");
    }

    @DisplayName("비밀번호가 틀리면 예외를 던진다")
    @Test
    void wrongPassword() {
        var account = new UserAccount(
                "admin01", "SH", "admin01", "Admin", "OTHER", "1", 0, "0", "N");
        when(userRepository.findByUserIdAndBusinessCode("admin01", "SH")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> loginUseCase.login(new LoginCommand("SH", "admin01", "bad")))
                .isInstanceOf(LoginException.class);

        verify(userRepository).updateLoginFailure(eq(account), eq(1), eq("0"));
    }
}
