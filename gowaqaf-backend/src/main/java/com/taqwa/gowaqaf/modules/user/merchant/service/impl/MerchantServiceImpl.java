package com.taqwa.gowaqaf.modules.user.merchant.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

	private final MerchantRepository merchantRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public MerchantRegisterResponse createMerchant(MerchantRegisterCredentials request) {
		Merchant user = new Merchant();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		// No roles.

		Merchant saved = merchantRepository.save(user);

		return new MerchantRegisterResponse(saved.getUsername(), saved.getEmail());
	}

}
