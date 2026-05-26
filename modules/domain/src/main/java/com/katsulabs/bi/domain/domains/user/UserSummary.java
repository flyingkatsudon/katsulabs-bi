package com.katsulabs.bi.domain.domains.user;

public record UserSummary(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        String userStatus) {}
