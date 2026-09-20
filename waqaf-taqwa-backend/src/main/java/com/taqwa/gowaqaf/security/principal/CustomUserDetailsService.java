package com.taqwa.gowaqaf.security.principal;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final AccountRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found."));

		Collection<? extends GrantedAuthority> roles = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();

		return new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword(), roles);
	}

}
