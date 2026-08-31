package com.taqwa.gowaqaf.security.user.admin.principal;

import java.util.Collection;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdminUserDetails implements AccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Admin admin;

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return admin.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
	}

	@Override
	public @Nullable String getPassword() {
		return admin.getPassword();
	}

	@Override
	public String getUsername() {
		return admin.getUsername();
	}

	@Override
	public UUID getId() {
		return admin.getId();
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.ADMIN;
	}

}
