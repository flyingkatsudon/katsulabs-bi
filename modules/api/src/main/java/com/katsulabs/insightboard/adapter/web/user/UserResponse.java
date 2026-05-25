package com.katsulabs.insightboard.adapter.web.user;

public record UserResponse(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        String userStatus) {}
