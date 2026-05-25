package org.cboard.security.authentication;

import java.util.Collection;

import org.cboard.dto.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class CboardAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final Object principal;
	private Object credentials;

	public CboardAuthenticationToken(Object principal) {
		super(null);
		this.principal = (User) principal;
		setAuthenticated(false);
	}

	public CboardAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = (User) principal;
		super.setAuthenticated(true);
	}

	public CboardAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.principal = (User) principal;
		this.credentials = credentials;
		setAuthenticated(false);
	}

	public CboardAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = (User) principal;
		this.credentials = credentials;
		super.setAuthenticated(true);
	}

	public Object getCredentials() {
		return this.credentials;
	}

	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
					"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}
		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		credentials = null;
	}
}
