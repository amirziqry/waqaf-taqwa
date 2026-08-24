package com.taqwa.gowaqaf.security.account;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("accountSecurity")
public class AccountSecurity {

	public Boolean isMember(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		return principal.getAccountType() == AccountType.MEMBER;
	}

	public Boolean isVendor(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		return principal.getAccountType() == AccountType.VENDOR;
	}

	public Boolean isDonator(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		return principal.getAccountType() == AccountType.DONATOR;
	}

}
