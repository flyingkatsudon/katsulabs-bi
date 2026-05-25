package com.katsulabs.insightboard.application.user;

import com.katsulabs.insightboard.application.common.ServiceResult;
import com.katsulabs.insightboard.domain.user.UserRepository;

public class DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ServiceResult execute(String userId) {
        if (userRepository.findByUserId(userId).isEmpty()) {
            return ServiceResult.fail("사용자를 찾을 수 없습니다.");
        }
        userRepository.softDelete(userId);
        return ServiceResult.success("사용자가 삭제되었습니다.");
    }
}
