package org.cboard.infrastructure.persistence.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserRow findByUserIdAndBusinessCode(
            @Param("userId") String userId, @Param("businessCode") String businessCode);

    List<Long> findResourceTypesByRoleId(@Param("roleId") String roleId);

    void updateLoginFailure(@Param("userId") String userId,
            @Param("businessCode") String businessCode,
            @Param("rbacPolicy") int rbacPolicy,
            @Param("userStateInfo") String userStateInfo);

    void resetLoginFailure(@Param("userId") String userId, @Param("businessCode") String businessCode);
}
