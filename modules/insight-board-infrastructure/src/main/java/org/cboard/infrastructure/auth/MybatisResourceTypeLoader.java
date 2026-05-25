package org.cboard.infrastructure.auth;

import java.util.Collections;
import java.util.List;

import org.cboard.application.auth.ResourceTypeLoader;
import org.cboard.infrastructure.persistence.mybatis.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class MybatisResourceTypeLoader implements ResourceTypeLoader {

    private final UserMapper userMapper;

    public MybatisResourceTypeLoader(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<Long> loadByRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return Collections.emptyList();
        }
        return userMapper.findResourceTypesByRoleId(roleId);
    }
}
