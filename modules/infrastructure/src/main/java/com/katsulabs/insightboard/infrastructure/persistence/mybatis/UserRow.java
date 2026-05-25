package com.katsulabs.insightboard.infrastructure.persistence.mybatis;

public class UserRow {

    private String userId;
    private String loginName;
    private String userName;
    private String userPassword;
    private Integer rbacPolicy;
    private String userStateInfo;
    private String delCd;
    private String roleId;

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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getRbacPolicy() {
        return rbacPolicy;
    }

    public void setRbacPolicy(Integer rbacPolicy) {
        this.rbacPolicy = rbacPolicy;
    }

    public String getUserStateInfo() {
        return userStateInfo;
    }

    public void setUserStateInfo(String userStateInfo) {
        this.userStateInfo = userStateInfo;
    }

    public String getDelCd() {
        return delCd;
    }

    public void setDelCd(String delCd) {
        this.delCd = delCd;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
