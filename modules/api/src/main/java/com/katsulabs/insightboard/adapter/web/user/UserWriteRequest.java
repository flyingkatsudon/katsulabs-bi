package com.katsulabs.insightboard.adapter.web.user;

public record UserWriteRequest(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String password) {}
