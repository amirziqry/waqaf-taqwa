package com.taqwa.gowaqaf.security.member.authentication;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.security.member.principal.MemberUserDetails;

public class MemberAuthenticationService {

	private final AuthenticationManager authenticationManager;

	public MemberAuthenticationService(
			@Qualifier("memberAuthenticationManager") AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	public MemberUserDetails login(String username, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		return (MemberUserDetails) authentication.getPrincipal();
	}

}
