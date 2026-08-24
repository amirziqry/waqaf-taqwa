package com.taqwa.gowaqaf.mockuser.donator;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.jwt.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockDonatorSecurityContextFactory implements WithSecurityContextFactory<WithMockDonator> {

	private final DonatorRepository donatorRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockDonator annotation) {
		Donator donator = new Donator();

		donator.setUsername(annotation.username());
		donator.setPassword(passwordEncoder.encode("0000"));
		donator.setEmail("test@gmail.com");

		Donator mock = donatorRepository.save(donator);

		JwtUserDetails principal = new JwtUserDetails(mock.getId(), mock.getUsername(), AccountType.DONATOR, null);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
