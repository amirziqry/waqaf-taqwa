package com.taqwa.gowaqaf.modules.auth.member.service.impl;

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

import com.taqwa.gowaqaf.modules.auth.member.dto.MemberAuthDetails;
import com.taqwa.gowaqaf.modules.auth.member.dto.MemberLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.member.service.MemberAuthService;
import com.taqwa.gowaqaf.security.jwt.JwtService;
import com.taqwa.gowaqaf.security.member.principal.MemberUserDetails;

@Service
public class MemberAuthServiceImpl implements MemberAuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public MemberAuthServiceImpl(@Qualifier("memberAuthenticationManager") AuthenticationManager authenticationManager,
			JwtService jwtService) {
		super();
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	public MemberAuthDetails login(MemberLoginCredentials request) {
		Authentication auth = null;

		try {
			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (AuthenticationException e) {
			throw new UsernameNotFoundException(null);
		}

		MemberUserDetails userDetails = (MemberUserDetails) auth.getPrincipal();

		Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		String token = jwtService.generateToken(userDetails);

		return new MemberAuthDetails(userDetails.getUsername(), null, roles, token);
	}

}
