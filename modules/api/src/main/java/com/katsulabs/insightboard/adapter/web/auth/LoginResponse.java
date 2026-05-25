package com.katsulabs.insightboard.adapter.web.auth;

import java.util.List;

public record LoginResponse(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        List<Long> resourceTypes,
        String sessionId) {}
