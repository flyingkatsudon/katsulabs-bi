package com.katsulabs.insightboard.domain.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<UserAccount> findByUserId(String userId);

    List<UserSummary> findAll();

    void insert(UserWriteCommand command, String passwordHash);

    void update(UserWriteCommand command, String passwordHashOrNull);

    void softDelete(String userId);

    void updateLoginFailure(UserAccount account, int failedCount, String userStateInfo);

    void resetLoginFailure(String userId);

    Optional<String> findRoleName(String roleId);
}
