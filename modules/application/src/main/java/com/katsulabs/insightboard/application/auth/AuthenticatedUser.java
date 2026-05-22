package com.katsulabs.insightboard.application.auth;

import java.util.List;

public record AuthenticatedUser(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        Long defaultBoardId,
        List<Long> resourceTypes) {}
