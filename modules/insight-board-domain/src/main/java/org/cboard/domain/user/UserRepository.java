package org.cboard.domain.user;

import java.util.Optional;

public interface UserRepository {

    Optional<UserAccount> findByUserIdAndBusinessCode(String userId, String businessCode);

    void updateLoginFailure(UserAccount account, int failedCount, String userStateInfo);

    void resetLoginFailure(String userId, String businessCode);
}
