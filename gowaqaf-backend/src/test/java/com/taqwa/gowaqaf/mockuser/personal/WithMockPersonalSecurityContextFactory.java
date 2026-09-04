package com.taqwa.gowaqaf.mockuser.personal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.jwt.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockPersonalSecurityContextFactory implements WithSecurityContextFactory<WithMockPersonal> {

	private final PersonalRepository personalRepository;
	private final AccountInfoRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockPersonal annotation) {
		Personal personal = new Personal();
		AccountInfo info = new AccountInfo();

		personal.setUsername(annotation.username());
		personal.setPassword(passwordEncoder.encode("0000"));

		info.setAccountHolderName("Jane Doe");
		info.setEmail("test@gmail.com");
		info.setPhone("60123456789");
		personal.setInfo(identityRepository.save(info));

		Personal mock = personalRepository.save(personal);

		JwtUserDetails principal = new JwtUserDetails(mock.getId(), mock.getUsername(), AccountType.PERSONAL, null);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
