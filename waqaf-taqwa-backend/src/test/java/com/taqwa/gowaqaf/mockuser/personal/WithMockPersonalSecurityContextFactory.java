package com.taqwa.gowaqaf.mockuser.personal;

import java.util.List;
import java.util.Random;
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
public class WithMockPersonalSecurityContextFactory implements WithSecurityContextFactory<WithMockPersonal> {

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockPersonal annotation) {
		Account user = new Account();

		user.setUsername(annotation.username());
		user.setPassword(passwordEncoder.encode("0000"));

		user.setAccountHolderName("Jane Doe");
		user.setEmail("test@gmail.com");
		user.setPhone("6012" + new Random().nextInt(10000000));
		user.setRoles(Set.of(Role.USER));

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
