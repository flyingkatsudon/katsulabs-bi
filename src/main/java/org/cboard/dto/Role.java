package org.cboard.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class Role implements UserDetails {
	
	protected String roleId;
	protected String roleName;
	protected ArrayList<Long> resTypeList;

	protected List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	
	protected boolean isAccountNonExpired = true;
	protected boolean isAccountNonLocked = true;
	protected boolean isCredentialsNonExpired = true;
	protected boolean isEnabled = true;
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
		this.authorities.add(new SimpleGrantedAuthority(roleId));
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return this.isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return this.isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return this.isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.isEnabled;
	}

	public ArrayList<Long> getResTypeList() {
		return resTypeList;
	}

	public void setResTypeList(ArrayList<Long> resTypeList) {
		this.resTypeList = resTypeList;
	}
}
