package com.katsulabs.insightboard.infrastructure.auth;

import java.util.Collections;
import java.util.List;

import com.katsulabs.insightboard.application.auth.ResourceTypeLoader;
import com.katsulabs.insightboard.infrastructure.persistence.mybatis.UserMapper;
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
