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

import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.modules.user.member.entity.Role;
import com.taqwa.gowaqaf.modules.user.member.repository.MemberRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.jwt.JwtUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WithMockMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public SecurityContext createSecurityContext(WithMockMember annotation) {
		Member member = new Member();

		member.setUsername(annotation.username());
		member.setPassword(passwordEncoder.encode("0000"));
		member.setEmail("test@gmail.com");

		Set<Role> roles = Arrays.stream(annotation.roles()).map(Role::valueOf).collect(Collectors.toSet());
		member.setRoles(roles);

		Member mock = memberRepository.save(member);

		List<GrantedAuthority> authorities = mock.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).collect(Collectors.toList());

		JwtUserDetails principal = new JwtUserDetails(mock.getId(), mock.getUsername(), AccountType.MEMBER,
				authorities);

		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
				principal.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();

		context.setAuthentication(authentication);

		return context;
	}

}
