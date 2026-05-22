package com.katsulabs.bi.domain.user;

public record UserWriteCommand(
        String userId,
        String loginName,
        String displayName,
        String roleId,
        String plainPassword) {}
