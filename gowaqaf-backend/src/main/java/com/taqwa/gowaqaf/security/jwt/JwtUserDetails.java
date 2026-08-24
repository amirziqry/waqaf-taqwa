package com.taqwa.gowaqaf.security.jwt;

import java.util.Collection;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtUserDetails implements AccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final UUID id;
	private final String username;
	private final AccountType accountType;
	private final Collection<? extends GrantedAuthority> authorities;

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
		return authorities;
	}

	@Override
	public @Nullable String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public AccountType getAccountType() {
		return accountType;
	}
}
