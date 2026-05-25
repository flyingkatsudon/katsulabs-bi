package com.katsulabs.insightboard.domain.user;

public record UserSummary(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        String userStatus) {}
