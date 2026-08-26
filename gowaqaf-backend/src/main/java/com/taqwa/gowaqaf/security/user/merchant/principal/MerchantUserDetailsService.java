package com.taqwa.gowaqaf.security.user.merchant.principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantUserDetailsService implements UserDetailsService {

	private final MerchantRepository merchantRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Merchant merchant = merchantRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Username not found."));

		return new MerchantUserDetails(merchant);
	}

}
