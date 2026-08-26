package com.taqwa.gowaqaf.modules.auth.admin.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminAuthDetails;
import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.admin.service.AdminAuthService;
import com.taqwa.gowaqaf.security.jwt.JwtService;
import com.taqwa.gowaqaf.security.user.admin.principal.AdminUserDetails;

@Service
public class AdminAuthServiceImpl implements AdminAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AdminAuthServiceImpl(@Qualifier("adminAuthenticationManager") AuthenticationManager authenticationManager,
			JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	public AdminAuthDetails login(AdminLoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		AdminUserDetails userDetails = (AdminUserDetails) auth.getPrincipal();

		Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		String token = jwtService.generateToken(userDetails);

		return new AdminAuthDetails(userDetails.getUsername(), null, roles, token);
	}

}
