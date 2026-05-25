package com.katsulabs.insightboard.domain.user;

public record UserWriteCommand(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String plainPassword) {}
