package com.taqwa.gowaqaf.modules.auth.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.auth.dto.AuthDetails;
import com.taqwa.gowaqaf.modules.auth.dto.LoginCredentials;
import com.taqwa.gowaqaf.modules.auth.service.AdminAuthService;
import com.taqwa.gowaqaf.security.jwt.JwtService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Override
	public AuthDetails login(LoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

		boolean isStaff = userDetails.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")
						|| authority.getAuthority().equals("ROLE_EDITOR") || authority.getAuthority().equals("ROLE_SUPER_ADMIN"));

		if (!isStaff) {
			throw new AccessDeniedException("Account is not allowed to use admin login");
		}

		Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		String token = jwtService.generateToken(userDetails);

		return new AuthDetails(userDetails.getUsername(), null, roles, token);
	}

}
