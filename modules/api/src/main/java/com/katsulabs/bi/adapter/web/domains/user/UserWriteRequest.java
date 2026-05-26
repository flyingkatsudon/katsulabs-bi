package com.katsulabs.bi.adapter.web.domains.user;

public record UserWriteRequest(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String password) {}
