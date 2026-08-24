package com.taqwa.gowaqaf.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.taqwa.gowaqaf.security.jwt.JwtFilter;

/**
 * Configuration for JWT filter registration.
 *
 * <p>
 * MemberJwtFilter and DonatorJwtFilter are managed as Spring beans for
 * dependency injection, but their automatic servlet filter registration is
 * disabled. This ensures that the filters are only executed when explicitly
 * added to their respective SecurityFilterChain using
 * {@code addFilterBefore(...)}.
 *
 * <p>
 * Without disabling automatic registration, Spring Boot may register these
 * filters globally in the servlet filter chain. This can cause multiple JWT
 * filters to process the same request, resulting in the wrong authentication
 * flow being applied (e.g. the Donator JWT filter intercepting Member
 * requests).
 *
 * <p>
 * By disabling servlet registration, each JWT filter is scoped exclusively to
 * its intended authentication domain as configured in SecurityConfig.
 */

@Configuration
public class FilterConfig {

	@Bean
	FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter filter) {
		FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(filter);

		registration.setEnabled(false);

		return registration;
	}

}
