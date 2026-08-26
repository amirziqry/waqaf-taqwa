package com.taqwa.gowaqaf.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import com.taqwa.gowaqaf.security.user.admin.authentication.AdminAuthenticationProvider;
import com.taqwa.gowaqaf.security.user.merchant.authentication.MerchantAuthenticationProvider;
import com.taqwa.gowaqaf.security.user.personal.authentication.PersonalAuthenticationProvider;

@Configuration
public class AuthenticationConfig {

	@Bean("personalAuthenticationManager")
	@Primary
	AuthenticationManager personalAuthenticationManager(PersonalAuthenticationProvider personalAuthenticationProvider) {
		return new ProviderManager(personalAuthenticationProvider);
	}

	@Bean("merchantAuthenticationManager")
	AuthenticationManager merchantAuthenticationManager(MerchantAuthenticationProvider merchantAuthenticationProvider) {
		return new ProviderManager(merchantAuthenticationProvider);
	}

	@Bean("adminAuthenticationManager")
	AuthenticationManager adminAuthenticationManager(AdminAuthenticationProvider adminAuthenticationProvider) {
		return new ProviderManager(adminAuthenticationProvider);
	}

}
