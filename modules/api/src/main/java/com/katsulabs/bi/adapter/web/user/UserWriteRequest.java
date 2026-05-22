package com.katsulabs.bi.adapter.web.user;

public record UserWriteRequest(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String password) {}
