package com.katsulabs.bi.application.domains.user;

import lombok.RequiredArgsConstructor;

import com.katsulabs.bi.application.common.ServiceResult;
import com.katsulabs.bi.domain.domains.user.UserRepository;

@RequiredArgsConstructor
public class DeleteUserUseCase {

    private final UserRepository userRepository;


    public ServiceResult execute(String userId) {
        if (userRepository.findByUserId(userId).isEmpty()) {
            return ServiceResult.fail("사용자를 찾을 수 없습니다.");
        }
        userRepository.softDelete(userId);
        return ServiceResult.success("사용자가 삭제되었습니다.");
    }
}
