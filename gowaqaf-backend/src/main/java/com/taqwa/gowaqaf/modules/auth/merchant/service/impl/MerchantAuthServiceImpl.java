package com.taqwa.gowaqaf.modules.auth.merchant.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.auth.merchant.dto.MerchantAuthDetails;
import com.taqwa.gowaqaf.modules.auth.merchant.dto.MerchantLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.merchant.service.MerchantAuthService;
import com.taqwa.gowaqaf.security.jwt.JwtService;
import com.taqwa.gowaqaf.security.user.merchant.principal.MerchantUserDetails;

@Service
public class MerchantAuthServiceImpl implements MerchantAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public MerchantAuthServiceImpl(@Qualifier("merchantAuthenticationManager") AuthenticationManager authenticationManager,
			JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	public MerchantAuthDetails login(MerchantLoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		MerchantUserDetails userDetails = (MerchantUserDetails) auth.getPrincipal();

		String token = jwtService.generateToken(userDetails);

		return new MerchantAuthDetails(userDetails.getUsername(), null, token);
	}
}
