package com.taqwa.gowaqaf.security.user.personal.principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalUserDetailsService implements UserDetailsService {

	private final PersonalRepository personalRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Personal personal = personalRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found."));

		return new PersonalUserDetails(personal);
	}

}
