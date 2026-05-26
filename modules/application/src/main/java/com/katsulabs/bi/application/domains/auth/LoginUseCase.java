package com.katsulabs.bi.application.domains.auth;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.domain.domains.user.UserAccount;
import com.katsulabs.bi.domain.domains.user.UserRepository;

/**
 * 레거시 v1AuthenticationProvider 규칙을 유스케이스로 이전.
 */
@RequiredArgsConstructor
public class LoginUseCase {

    private static final String INVALID_CREDENTIALS = "사용자 ID 또는 비밀번호가 일치하지 않습니다.";

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final ResourceTypeLoader resourceTypeLoader;


    public AuthenticatedUser login(LoginCommand command) {
        validate(command);

        UserAccount account = userRepository
                .findByUserId(command.userId())
                .orElseThrow(() -> new LoginException(INVALID_CREDENTIALS));

        if (account.isDeleted()) {
            throw new LoginException(INVALID_CREDENTIALS);
        }

        if (account.isLocked()) {
            userRepository.updateLoginFailure(account, 5, "1");
            throw new LoginException("비밀번호가 5회 이상 일치하지 않았습니다. 시스템 관리자에게 문의하세요.");
        }

        String hashed = passwordHasher.hash(command.plainPassword());
        if (!hashed.equalsIgnoreCase(account.passwordHash())) {
            int failCount = account.failedLoginCount() + 1;
            String state = failCount >= 5 ? "1" : "0";
            userRepository.updateLoginFailure(account, failCount, state);
            if (failCount >= 5) {
                throw new LoginException("비밀번호가 5회 이상 일치하지 않았습니다. 시스템 관리자에게 문의하세요.");
            }
            throw new LoginException("비밀번호가 " + failCount + "회 일치하지 않았습니다.");
        }

        userRepository.resetLoginFailure(command.userId());

        String roleName = userRepository
                .findRoleName(account.roleId())
                .orElse(account.roleId());

        return new AuthenticatedUser(
                account.userId(),
                account.loginName(),
                account.displayName(),
                account.roleId(),
                roleName,
                account.defaultBoardId(),
                resourceTypeLoader.loadByRoleId(account.roleId()));
    }

    private static void validate(LoginCommand command) {
        if (command.userId() == null || command.userId().isBlank()) {
            throw new LoginException("사용자 ID를 입력해주세요.");
        }
        if (command.userId().length() < 6 || command.userId().length() > 10) {
            throw new LoginException("사용자 ID는 6~10자여야 합니다.");
        }
        if (command.userId().contains(" ")) {
            throw new LoginException("사용자 ID에 공백을 포함할 수 없습니다.");
        }
        if (command.plainPassword() == null || command.plainPassword().isBlank()) {
            throw new LoginException("비밀번호를 입력해주세요.");
        }
    }
}
