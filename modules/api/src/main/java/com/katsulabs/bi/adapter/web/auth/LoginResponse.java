package com.katsulabs.bi.adapter.web.auth;

import java.util.List;

public record LoginResponse(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String roleName,
        Long defaultBoardId,
        List<Long> resourceTypes,
        String sessionId) {}
