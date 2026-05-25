package com.katsulabs.insightboard.application.user;

import java.util.List;

import com.katsulabs.insightboard.domain.user.UserRepository;
import com.katsulabs.insightboard.domain.user.UserSummary;

public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummary> execute() {
        return userRepository.findAll();
    }
}
