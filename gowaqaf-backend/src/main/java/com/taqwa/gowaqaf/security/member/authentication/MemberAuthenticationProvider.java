package com.taqwa.gowaqaf.security.member.authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.taqwa.gowaqaf.security.member.principal.MemberUserDetails;
import com.taqwa.gowaqaf.security.member.principal.MemberUserDetailsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberAuthenticationProvider implements AuthenticationProvider {

	private final MemberUserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		MemberUserDetails userDetails = (MemberUserDetails) userDetailsService.loadUserByUsername(username);

		if (!passwordEncoder.matches(password, userDetails.getPassword()))
			throw new BadCredentialsException("Invalid username or password");

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
