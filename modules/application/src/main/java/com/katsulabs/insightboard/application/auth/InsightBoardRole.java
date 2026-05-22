package com.katsulabs.insightboard.application.auth;

/**
 * dashboard_role.role_id — V8 시드 기준.
 */
public enum InsightBoardRole {
    SUPER_ADMIN("1"),
    VIEWER("2"),
    MANAGER("3");

    private final String roleId;

    InsightBoardRole(String roleId) {
        this.roleId = roleId;
    }

    public String roleId() {
        return roleId;
    }

    public static InsightBoardRole fromRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return VIEWER;
        }
        for (InsightBoardRole role : values()) {
            if (role.roleId.equals(roleId)) {
                return role;
            }
        }
        return VIEWER;
    }

    public boolean canManageUsers() {
        return this == SUPER_ADMIN;
    }

    public boolean canWriteDatasourceOrDataset() {
        return this == SUPER_ADMIN;
    }

    public boolean canWriteDashboardContent() {
        return this == SUPER_ADMIN || this == MANAGER;
    }

    public boolean canPublishBoardToViewers() {
        return canWriteDashboardContent();
    }
}
