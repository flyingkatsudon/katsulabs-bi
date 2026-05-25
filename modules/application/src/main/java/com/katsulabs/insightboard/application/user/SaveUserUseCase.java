package com.katsulabs.insightboard.application.user;

import java.util.List;

import com.katsulabs.insightboard.application.auth.InsightBoardRole;
import com.katsulabs.insightboard.application.auth.PasswordHasher;
import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.user.UserRepository;
import com.katsulabs.insightboard.domain.user.UserWriteCommand;

public class SaveUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public SaveUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public ServiceResult execute(UserWriteCommand command) {
        validate(command, true);
        if (userRepository.findByUserId(command.userId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID 입니다: " + command.userId());
        }
        String hash = passwordHasher.hash(command.plainPassword());
        userRepository.insert(command, hash);
        return ServiceResult.success("사용자가 등록되었습니다.");
    }

    static void validate(UserWriteCommand command, boolean requirePassword) {
        if (command.userId() == null || command.userId().isBlank()) {
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }
        if (command.userId().length() < 6 || command.userId().length() > 10) {
            throw new IllegalArgumentException("사용자 ID는 6~10자여야 합니다.");
        }
        if (command.roleId() == null
                || !List.of(
                                InsightBoardRole.SUPER_ADMIN.roleId(),
                                InsightBoardRole.VIEWER.roleId(),
                                InsightBoardRole.MANAGER.roleId())
                        .contains(command.roleId())) {
            throw new IllegalArgumentException("유효한 역할이 필요합니다.");
        }
        if (requirePassword && (command.plainPassword() == null || command.plainPassword().isBlank())) {
            throw new IllegalArgumentException("비밀번호가 필요합니다.");
        }
    }
}
