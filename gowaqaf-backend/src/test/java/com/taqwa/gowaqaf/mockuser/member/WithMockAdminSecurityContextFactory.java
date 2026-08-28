package com.taqwa.gowaqaf.mockuser.member;

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

import com.taqwa.gowaqaf.modules.user.account.entity.AccountIdentity;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountIdentityRepository;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.jwt.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockAdminSecurityContextFactory implements WithSecurityContextFactory<WithMockAdmin> {

	private final AdminRepository adminRepository;
	private final AccountIdentityRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockAdmin annotation) {
		Admin admin = new Admin();
		AccountIdentity identity = new AccountIdentity();

		admin.setUsername(annotation.username());
		admin.setPassword(passwordEncoder.encode("0000"));

		identity.setEmail("test@gmail.com");
		admin.setIdentity(identityRepository.save(identity));

		Set<Role> roles = Arrays.stream(annotation.roles()).map(Role::valueOf).collect(Collectors.toSet());
		admin.setRoles(roles);

		Admin mock = adminRepository.save(admin);

		List<GrantedAuthority> authorities = mock.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList());

		JwtUserDetails principal = new JwtUserDetails(mock.getId(), mock.getUsername(), AccountType.ADMIN, authorities);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
