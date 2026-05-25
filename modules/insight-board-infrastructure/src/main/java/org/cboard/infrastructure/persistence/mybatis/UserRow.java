package org.cboard.infrastructure.persistence.mybatis;

public class UserRow {

    private String userId;
    private String businessCode;
    private String loginName;
    private String userName;
    private String userPassword;
    private String roleId;
    private Integer rbacPolicy;
    private String userStateInfo;
    private String delCd;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
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

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
}
