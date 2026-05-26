package com.katsulabs.bi.infrastructure.domains.auth;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

import com.katsulabs.bi.application.domains.auth.ResourceTypeLoader;
import com.katsulabs.bi.infrastructure.domains.user.persistence.mybatis.UserMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MybatisResourceTypeLoader implements ResourceTypeLoader {

    private final UserMapper userMapper;


    @Override
    public List<Long> loadByRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return Collections.emptyList();
        }
        return userMapper.findResourceTypesByRoleId(roleId);
    }
}
