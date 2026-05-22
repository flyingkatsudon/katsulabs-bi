package com.katsulabs.bi.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import com.katsulabs.bi.domain.user.UserAccount;
import com.katsulabs.bi.domain.user.UserRepository;
import com.katsulabs.bi.domain.user.UserSummary;
import com.katsulabs.bi.domain.user.UserWriteCommand;
import com.katsulabs.bi.infrastructure.persistence.mybatis.UserMapper;
import com.katsulabs.bi.infrastructure.persistence.mybatis.UserRow;
import com.katsulabs.bi.infrastructure.persistence.mybatis.UserSummaryRow;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserAccount> findByUserId(String userId) {
        UserRow row = userMapper.findByUserId(userId);
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(toAccount(row));
    }

    @Override
    public List<UserSummary> findAll() {
        return userMapper.findAll().stream().map(UserRepositoryImpl::toSummary).toList();
    }

    @Override
    public Optional<String> findRoleName(String roleId) {
        String name = userMapper.findRoleName(roleId);
        return Optional.ofNullable(name);
    }

    @Override
    public void insert(UserWriteCommand command, String passwordHash) {
        userMapper.insertUser(
                command.userId(),
                command.loginName() != null ? command.loginName() : command.userId(),
                command.displayName() != null ? command.displayName() : command.userId(),
                passwordHash);
        userMapper.insertUserRole(command.userId(), command.roleId());
    }

    @Override
    public void update(UserWriteCommand command, String passwordHashOrNull) {
        userMapper.updateUser(
                command.userId(),
                command.loginName() != null ? command.loginName() : command.userId(),
                command.displayName() != null ? command.displayName() : command.userId(),
                passwordHashOrNull);
        userMapper.updateUserRole(command.userId(), command.roleId());
    }

    @Override
    public void softDelete(String userId) {
        userMapper.softDeleteUser(userId);
    }

    @Override
    public void updateLoginFailure(UserAccount account, int failedCount, String userStateInfo) {
        userMapper.updateLoginFailure(account.userId(), failedCount, userStateInfo);
    }

    @Override
    public void resetLoginFailure(String userId) {
        userMapper.resetLoginFailure(userId);
    }

    private static UserAccount toAccount(UserRow row) {
        int failed = row.getRbacPolicy() == null ? 0 : row.getRbacPolicy();
        return new UserAccount(
                row.getUserId(),
                row.getLoginName(),
                row.getUserName(),
                row.getUserPassword(),
                row.getRoleId(),
                row.getDefaultBoardId(),
                failed,
                row.getUserStateInfo(),
                row.getDelCd());
    }

    private static UserSummary toSummary(UserSummaryRow row) {
        return new UserSummary(
                row.getUserId(),
                row.getLoginName(),
                row.getUserName(),
                row.getRoleId(),
                row.getRoleName(),
                row.getUserStatus());
    }
}
