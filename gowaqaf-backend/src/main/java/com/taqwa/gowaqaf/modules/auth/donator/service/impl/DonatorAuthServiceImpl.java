package com.taqwa.gowaqaf.modules.auth.donator.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorAuthDetails;
import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.donator.service.DonatorAuthService;
import com.taqwa.gowaqaf.security.donator.principal.DonatorUserDetails;
import com.taqwa.gowaqaf.security.jwt.JwtService;

@Service
public class DonatorAuthServiceImpl implements DonatorAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public DonatorAuthServiceImpl(
			@Qualifier("donatorAuthenticationManager") AuthenticationManager authenticationManager,
			JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	public DonatorAuthDetails login(DonatorLoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		DonatorUserDetails userDetails = (DonatorUserDetails) auth.getPrincipal();

		String token = jwtService.generateToken(userDetails);

		return new DonatorAuthDetails(userDetails.getUsername(), null, token);
	}
}
