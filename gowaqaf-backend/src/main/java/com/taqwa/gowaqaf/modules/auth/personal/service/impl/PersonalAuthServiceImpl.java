package com.taqwa.gowaqaf.modules.auth.personal.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalAuthDetails;
import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.personal.service.PersonalAuthService;
import com.taqwa.gowaqaf.security.jwt.JwtService;
import com.taqwa.gowaqaf.security.user.personal.principal.PersonalUserDetails;

@Service
public class PersonalAuthServiceImpl implements PersonalAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public PersonalAuthServiceImpl(
			@Qualifier("personalAuthenticationManager") AuthenticationManager authenticationManager,
			JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	public PersonalAuthDetails login(PersonalLoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		PersonalUserDetails userDetails = (PersonalUserDetails) auth.getPrincipal();

		String token = jwtService.generateToken(userDetails);

		return new PersonalAuthDetails(userDetails.getUsername(), null, token);
	}
}
