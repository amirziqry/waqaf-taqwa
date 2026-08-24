package com.taqwa.gowaqaf.modules.auth.vendor.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorAuthDetails;
import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.vendor.service.VendorAuthService;
import com.taqwa.gowaqaf.security.jwt.JwtService;
import com.taqwa.gowaqaf.security.vendor.principal.VendorUserDetails;

@Service
public class VendorAuthServiceImpl implements VendorAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public VendorAuthServiceImpl(@Qualifier("vendorAuthenticationManager") AuthenticationManager authenticationManager,
			JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	public VendorAuthDetails login(VendorLoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		VendorUserDetails userDetails = (VendorUserDetails) auth.getPrincipal();

		String token = jwtService.generateToken(userDetails);

		return new VendorAuthDetails(userDetails.getUsername(), null, token);
	}
}
