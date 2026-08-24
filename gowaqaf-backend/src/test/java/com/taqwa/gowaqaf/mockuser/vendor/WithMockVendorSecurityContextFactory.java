package com.taqwa.gowaqaf.mockuser.vendor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.jwt.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockVendorSecurityContextFactory implements WithSecurityContextFactory<WithMockVendor> {

	private final VendorRepository vendorRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockVendor annotation) {
		Vendor vendor = new Vendor();

		vendor.setUsername(annotation.username());
		vendor.setPassword(passwordEncoder.encode("0000"));
		vendor.setEmail("test@gmail.com");

		Vendor mock = vendorRepository.save(vendor);

		JwtUserDetails principal = new JwtUserDetails(mock.getId(), mock.getUsername(), AccountType.VENDOR, null);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
