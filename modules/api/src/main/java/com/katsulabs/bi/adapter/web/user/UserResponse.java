package com.katsulabs.bi.adapter.web.user;

public record UserResponse(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        String userStatus) {}
