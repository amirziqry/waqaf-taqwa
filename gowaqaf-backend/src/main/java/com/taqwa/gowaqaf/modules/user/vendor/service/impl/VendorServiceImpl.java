package com.taqwa.gowaqaf.modules.user.vendor.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.vendor.dto.VendorRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.vendor.dto.VendorRegisterResponse;
import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;
import com.taqwa.gowaqaf.modules.user.vendor.service.VendorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

	private final VendorRepository vendorRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public VendorRegisterResponse createDonator(VendorRegisterCredentials request) {
		Vendor user = new Vendor();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		// No roles.

		Vendor saved = vendorRepository.save(user);

		return new VendorRegisterResponse(saved.getUsername(), saved.getEmail());
	}

}
