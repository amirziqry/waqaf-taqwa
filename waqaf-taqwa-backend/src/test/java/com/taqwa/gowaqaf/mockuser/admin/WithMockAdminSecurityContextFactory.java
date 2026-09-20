package com.taqwa.gowaqaf.mockuser.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockAdminSecurityContextFactory implements WithSecurityContextFactory<WithMockAdmin> {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockAdmin annotation) {
		Account user = new Account();

		user.setUsername(annotation.username());
		user.setPassword(passwordEncoder.encode("0000"));

		user.setEmail("test@gmail.com");

		Set<Role> roles = Arrays.stream(annotation.roles()).map(Role::valueOf).collect(Collectors.toSet());
		user.setRoles(roles);

		Account mock = accountRepository.save(user);

		List<GrantedAuthority> authorities = mock.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList());

		CustomUserDetails principal = new CustomUserDetails(mock.getId(), mock.getUsername(), null, authorities);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
