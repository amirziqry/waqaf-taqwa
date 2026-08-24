package com.taqwa.gowaqaf.security.vendor.principal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorUserDetailsService implements UserDetailsService {

	private final VendorRepository vendorRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Vendor vendor = vendorRepository.findByUsername(username);
		if (vendor == null)
			throw new UsernameNotFoundException("Username not found.");

		return new VendorUserDetails(vendor);
	}

}
