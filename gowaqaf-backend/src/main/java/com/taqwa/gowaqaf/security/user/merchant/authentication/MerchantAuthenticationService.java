package com.taqwa.gowaqaf.security.user.merchant.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.security.user.merchant.principal.MerchantUserDetails;

public class MerchantAuthenticationService {

	private final AuthenticationManager authenticationManager;

	public MerchantAuthenticationService(
			@Qualifier("merchantAuthenticationManager") AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public MerchantUserDetails login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return (MerchantUserDetails) authentication.getPrincipal();
	}

}
