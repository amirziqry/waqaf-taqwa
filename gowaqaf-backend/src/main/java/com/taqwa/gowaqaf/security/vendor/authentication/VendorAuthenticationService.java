package com.taqwa.gowaqaf.security.vendor.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.security.vendor.principal.VendorUserDetails;

public class VendorAuthenticationService {

	private final AuthenticationManager authenticationManager;

	public VendorAuthenticationService(
			@Qualifier("vendorAuthenticationManager") AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public VendorUserDetails login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return (VendorUserDetails) authentication.getPrincipal();
	}

}
