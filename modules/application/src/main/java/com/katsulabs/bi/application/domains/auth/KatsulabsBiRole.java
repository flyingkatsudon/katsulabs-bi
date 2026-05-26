package com.katsulabs.bi.application.domains.auth;

/**
 * dashboard_role.role_id — V8 시드 기준.
 */
public enum KatsulabsBiRole {
    SUPER_ADMIN("1"),
    VIEWER("2"),
    MANAGER("3");

    private final String roleId;

    KatsulabsBiRole(String roleId) {
        this.roleId = roleId;
    }

    public String roleId() {
        return roleId;
    }

    public static KatsulabsBiRole fromRoleId(String roleId) {
        if (roleId == null || roleId.isBlank()) {
            return VIEWER;
        }
        for (KatsulabsBiRole role : values()) {
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
