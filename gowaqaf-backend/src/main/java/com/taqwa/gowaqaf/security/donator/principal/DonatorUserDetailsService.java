package com.taqwa.gowaqaf.security.donator.principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonatorUserDetailsService implements UserDetailsService {

	private final DonatorRepository donatorRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Donator donator = donatorRepository.findByUsername(username);
		if (donator == null)
			throw new UsernameNotFoundException("Username not found.");

		return new DonatorUserDetails(donator);
	}

}
