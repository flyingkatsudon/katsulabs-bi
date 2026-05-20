package org.cboard.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by yfyuan on 2016/9/29.
 */

public class User extends Role {

	private String userId;
	private String userPassword;
	
	private String userName;
	private String loginName;
	private String businessCode;
	
	private int rbacPolicy;
	private String userStateInfo;
	
	private String oldUserPassword;
	private String checkPassword;
	private String loginIp;
	
	private String delCd;
	
	public String getLoginIp() {
		return loginIp;
	}
	
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	
	public String getCheckPassword() {
		return checkPassword;
	}

	public void setCheckPassword(String checkPassword) {
		this.checkPassword = checkPassword;
	}

	public String getOldUserPassword() {
		return oldUserPassword;
	}

	public void setOldUserPassword(String oldUserPassword) {
		this.oldUserPassword = oldUserPassword;
	}

	public User() {

	}

	public User(String userId) {
		this.userId = userId;
	}

	public User(String userId, String userPassword) {
		this.userId = userId;
		this.userPassword = userPassword;
	}

	public User(String userId, String userPassword, Collection<? extends GrantedAuthority> authorities) {
		this(userId, userPassword, true, true, true, true, authorities);
	}

	public User(String userId, String userPassword, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities) {

		if (((userId == null) || "".equals(userId)) || (userPassword == null)) {
			throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
		}

		this.userId = userId;
		this.userPassword = userPassword;
		this.isEnabled = enabled;
		this.isAccountNonExpired = accountNonExpired;
		this.isCredentialsNonExpired = credentialsNonExpired;
		this.isAccountNonLocked = accountNonLocked;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getName() {
		return userName;
	}

	public void setName(String userName) {
		this.userName = userName;
	}

	// v0 ~ v4
	private String v0;
	private String v1;
	private String v2;
	private String v3;

	public String getV0() {
		return v0;
	}

	public void setV0(String v0) {
		this.v0 = v0;
	}

	public String getV1() {
		return v1;
	}

	public void setV1(String v1) {
		this.v1 = v1;
	}

	public String getV2() {
		return v2;
	}

	public void setV2(String v2) {
		this.v2 = v2;
	}

	public String getV3() {
		return v3;
	}

	public void setV3(String v3) {
		this.v3 = v3;
	}
	
	public String getUserName() {
		return userName;
	}
	
	@Override
	public String getUsername() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public int getRbacPolicy() {
		return rbacPolicy;
	}

	public void setRbacPolicy(int rbacPolicy) {
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
