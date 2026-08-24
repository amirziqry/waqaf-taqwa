package com.taqwa.gowaqaf.security.member.principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.modules.user.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByUsername(username);
		if (member == null)
			throw new UsernameNotFoundException("Username not found.");

		return new MemberUserDetails(member);
	}

}
