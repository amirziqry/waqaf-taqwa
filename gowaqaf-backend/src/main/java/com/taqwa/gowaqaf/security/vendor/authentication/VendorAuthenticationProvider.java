package com.taqwa.gowaqaf.security.vendor.authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.taqwa.gowaqaf.security.vendor.principal.VendorUserDetails;
import com.taqwa.gowaqaf.security.vendor.principal.VendorUserDetailsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VendorAuthenticationProvider implements AuthenticationProvider {

	private final VendorUserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		VendorUserDetails userDetails = (VendorUserDetails) userDetailsService.loadUserByUsername(username);

		if (!passwordEncoder.matches(password, userDetails.getPassword()))
			throw new BadCredentialsException("Invalid username or password");

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
