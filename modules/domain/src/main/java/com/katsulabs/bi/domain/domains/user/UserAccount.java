package com.katsulabs.bi.domain.domains.user;

public record UserAccount(
        String userId,
        String loginName,
        String displayName,
        String passwordHash,
        String roleId,
        Long defaultBoardId,
        int failedLoginCount,
        String userStateInfo,
        String delCd) {

    public boolean isDeleted() {
        return delCd != null && "Y".equalsIgnoreCase(delCd.trim());
    }

    public boolean isLocked() {
        return failedLoginCount >= 5 || "1".equals(userStateInfo);
    }
}
