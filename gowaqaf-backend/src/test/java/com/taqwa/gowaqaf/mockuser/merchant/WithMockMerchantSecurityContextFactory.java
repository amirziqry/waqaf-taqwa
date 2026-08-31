package com.taqwa.gowaqaf.mockuser.merchant;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.jwt.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockMerchantSecurityContextFactory implements WithSecurityContextFactory<WithMockMerchant> {

	private final MerchantRepository merchantRepository;
	private final AccountInfoRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockMerchant annotation) {
		Merchant merchant = new Merchant();
		AccountInfo info = new AccountInfo();

		merchant.setUsername(annotation.username());
		merchant.setPassword(passwordEncoder.encode("0000"));

		info.setEmail("test@gmail.com");
		merchant.setInfo(identityRepository.save(info));

		Merchant mock = merchantRepository.save(merchant);

		JwtUserDetails principal = new JwtUserDetails(mock.getId(), mock.getUsername(), AccountType.MERCHANT, null);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
