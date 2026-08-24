package com.taqwa.gowaqaf.security.member.principal;

import java.util.Collection;
import java.util.UUID;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberUserDetails implements AccountUserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Member member;

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
		return member.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
	}

	@Override
	public @Nullable String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getUsername();
	}

	@Override
	public UUID getId() {
		return member.getId();
	}

	@Override
	public AccountType getAccountType() {
		return AccountType.MEMBER;
	}

	public String getEmail() {
		return member.getEmail();
	}

}
