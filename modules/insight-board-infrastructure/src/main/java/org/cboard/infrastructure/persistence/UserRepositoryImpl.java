package org.cboard.infrastructure.persistence;

import java.util.Optional;

import org.cboard.domain.user.UserAccount;
import org.cboard.domain.user.UserRepository;
import org.cboard.infrastructure.persistence.mybatis.UserMapper;
import org.cboard.infrastructure.persistence.mybatis.UserRow;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserAccount> findByUserIdAndBusinessCode(String userId, String businessCode) {
        UserRow row = userMapper.findByUserIdAndBusinessCode(userId, businessCode);
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(toAccount(row));
    }

    @Override
    public void updateLoginFailure(UserAccount account, int failedCount, String userStateInfo) {
        userMapper.updateLoginFailure(
                account.userId(), account.businessCode(), failedCount, userStateInfo);
    }

    @Override
    public void resetLoginFailure(String userId, String businessCode) {
        userMapper.resetLoginFailure(userId, businessCode);
    }

    private static UserAccount toAccount(UserRow row) {
        int failed = row.getRbacPolicy() == null ? 0 : row.getRbacPolicy();
        return new UserAccount(
                row.getUserId(),
                row.getBusinessCode(),
                row.getLoginName(),
                row.getUserName(),
                row.getUserPassword(),
                row.getRoleId(),
                failed,
                row.getUserStateInfo(),
                row.getDelCd());
    }
}
