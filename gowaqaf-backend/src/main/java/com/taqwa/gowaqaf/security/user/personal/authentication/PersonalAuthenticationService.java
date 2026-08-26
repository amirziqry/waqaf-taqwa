package com.taqwa.gowaqaf.security.user.personal.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.security.user.personal.principal.PersonalUserDetails;

public class PersonalAuthenticationService {

	private final AuthenticationManager authenticationManager;

	public PersonalAuthenticationService(
			@Qualifier("personalAuthenticationManager") AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public PersonalUserDetails login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return (PersonalUserDetails) authentication.getPrincipal();
	}

}
