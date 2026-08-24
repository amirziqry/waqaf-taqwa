package com.taqwa.gowaqaf.security.donator.principal;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DonatorUserDetails implements AccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Donator donator;

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
		return Collections.emptyList();
	}

	@Override
	public @Nullable String getPassword() {
		return donator.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return donator.getUsername();
	}

	@Override
	public UUID getId() {
		return donator.getId();
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.DONATOR;
	}

}
