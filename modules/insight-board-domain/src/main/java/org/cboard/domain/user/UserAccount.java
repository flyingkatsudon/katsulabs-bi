package org.cboard.domain.user;

/**
 * 인증·권한에 필요한 사용자 스냅샷.
 */
public record UserAccount(
        String userId,
        String businessCode,
        String loginName,
        String displayName,
        String passwordHash,
        String roleId,
        int failedLoginCount,
        String userStateInfo,
        String deleteCode) {

    public boolean isDeleted() {
        return deleteCode != null && deleteCode.equalsIgnoreCase("X");
    }

    public boolean isLocked() {
        return "1".equals(userStateInfo) || failedLoginCount >= 5;
    }
}
