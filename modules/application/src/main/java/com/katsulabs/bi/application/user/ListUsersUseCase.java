package com.katsulabs.bi.application.user;

import lombok.RequiredArgsConstructor;

import java.util.List;

import com.katsulabs.bi.domain.user.UserRepository;
import com.katsulabs.bi.domain.user.UserSummary;

@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserRepository userRepository;


    public List<UserSummary> execute() {
        return userRepository.findAll();
    }
}
