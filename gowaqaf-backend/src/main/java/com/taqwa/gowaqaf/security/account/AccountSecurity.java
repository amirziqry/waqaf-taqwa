package com.taqwa.gowaqaf.security.account;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("accountSecurity")
public class AccountSecurity {

	public Boolean isAdmin(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		return principal.getAccountType() == AccountType.ADMIN;
	}

	public Boolean isMerchant(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		return principal.getAccountType() == AccountType.MERCHANT;
	}

	public Boolean isPersonal(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		return principal.getAccountType() == AccountType.PERSONAL;
	}

}
