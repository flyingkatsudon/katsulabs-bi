package com.katsulabs.bi.application.user;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.auth.PasswordHasher;
import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.user.UserRepository;
import com.katsulabs.bi.domain.user.UserWriteCommand;

@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;


    public ServiceResult execute(UserWriteCommand command) {
        SaveUserUseCase.validate(command, false);
        if (userRepository
                .findByUserId(command.userId())
                .isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + command.userId());
        }
        String hash = null;
        if (command.plainPassword() != null && !command.plainPassword().isBlank()) {
            hash = passwordHasher.hash(command.plainPassword());
        }
        userRepository.update(command, hash);
        return ServiceResult.success("사용자 정보가 수정되었습니다.");
    }
}
