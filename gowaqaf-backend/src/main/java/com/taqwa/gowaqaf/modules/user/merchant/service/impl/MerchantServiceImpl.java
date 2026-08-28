package com.taqwa.gowaqaf.modules.user.merchant.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountIdentity;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.mapper.MerchantMapper;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

	private final MerchantRepository merchantRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public MerchantRegisterResponse createMerchant(MerchantRegisterCredentials dto) {
		Merchant user = new Merchant();
		AccountIdentity identity = new AccountIdentity();

		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		identity.setEmail(dto.getEmail());
		identity.setPhone(dto.getPhone());
		user.setIdentity(identity);

		Merchant saved = merchantRepository.save(user);

		return MerchantMapper.mapToRegisterResponse(saved);
	}

	@Override
	public Merchant getMerchantByUsername(String username) {
		Merchant merchant = merchantRepository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MER001, String.format("User %s not found.", username)));

		return merchant;
	}

}
