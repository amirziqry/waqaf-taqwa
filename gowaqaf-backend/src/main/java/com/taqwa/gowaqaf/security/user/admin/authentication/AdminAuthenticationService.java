package com.taqwa.gowaqaf.security.user.admin.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.security.user.admin.principal.AdminUserDetails;

public class AdminAuthenticationService {

	private final AuthenticationManager authenticationManager;

	public AdminAuthenticationService(
			@Qualifier("personalAuthenticationManager") AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public AdminUserDetails login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return (AdminUserDetails) authentication.getPrincipal();
	}

}
