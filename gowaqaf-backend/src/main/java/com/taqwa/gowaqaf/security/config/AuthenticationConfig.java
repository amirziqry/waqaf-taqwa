package com.taqwa.gowaqaf.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import com.taqwa.gowaqaf.security.donator.authentication.DonatorAuthenticationProvider;
import com.taqwa.gowaqaf.security.member.authentication.MemberAuthenticationProvider;
import com.taqwa.gowaqaf.security.vendor.authentication.VendorAuthenticationProvider;

@Configuration
public class AuthenticationConfig {

	@Bean("donatorAuthenticationManager")
	@Primary
	AuthenticationManager donatorAuthenticationManager(DonatorAuthenticationProvider donatorAuthenticationProvider) {
		return new ProviderManager(donatorAuthenticationProvider);
	}

	@Bean("vendorAuthenticationManager")
	AuthenticationManager vendorAuthenticationManager(VendorAuthenticationProvider vendorAuthenticationProvider) {
		return new ProviderManager(vendorAuthenticationProvider);
	}

	@Bean("memberAuthenticationManager")
	AuthenticationManager memberAuthenticationManager(MemberAuthenticationProvider memberAuthenticationProvider) {
		return new ProviderManager(memberAuthenticationProvider);
	}

}
