package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

public class UserSummaryRow {

    private String userId;
    private String loginName;
    private String userName;
    private String roleId;
    private String roleName;
    private String userStatus;
    private String delCd;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getDelCd() {
        return delCd;
    }

    public void setDelCd(String delCd) {
        this.delCd = delCd;
    }
}
