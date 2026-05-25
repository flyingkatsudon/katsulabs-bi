package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserRow findByUserId(@Param("userId") String userId);

    List<Long> findResourceTypesByRoleId(@Param("roleId") String roleId);

    List<UserSummaryRow> findAll();

    void insertUser(
            @Param("userId") String userId,
            @Param("loginName") String loginName,
            @Param("userName") String userName,
            @Param("passwordHash") String passwordHash);

    void insertUserRole(@Param("userId") String userId, @Param("roleId") String roleId);

    void updateUser(
            @Param("userId") String userId,
            @Param("loginName") String loginName,
            @Param("userName") String userName,
            @Param("passwordHash") String passwordHash);

    void updateUserRole(@Param("userId") String userId, @Param("roleId") String roleId);

    void softDeleteUser(@Param("userId") String userId);

    void updateLoginFailure(
            @Param("userId") String userId,
            @Param("rbacPolicy") int rbacPolicy,
            @Param("userStateInfo") String userStateInfo);

    void resetLoginFailure(@Param("userId") String userId);

    String findRoleName(@Param("roleId") String roleId);
}
