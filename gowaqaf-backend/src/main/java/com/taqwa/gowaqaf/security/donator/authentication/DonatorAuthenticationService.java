package com.taqwa.gowaqaf.security.donator.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.security.donator.principal.DonatorUserDetails;

public class DonatorAuthenticationService {

	private final AuthenticationManager authenticationManager;

	public DonatorAuthenticationService(
			@Qualifier("donatorAuthenticationManager") AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public DonatorUserDetails login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return (DonatorUserDetails) authentication.getPrincipal();
	}

}
