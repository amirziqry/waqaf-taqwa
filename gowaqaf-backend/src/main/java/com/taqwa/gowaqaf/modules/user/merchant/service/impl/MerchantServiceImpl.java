package com.taqwa.gowaqaf.modules.user.merchant.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
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
	private final AccountInfoRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public MerchantRegisterResponse createMerchant(MerchantRegisterCredentials dto) {
		Merchant user = new Merchant();
		AccountInfo info = new AccountInfo();

		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		info.setEmail(dto.getEmail());
		info.setPhone(dto.getPhone());
		user.setInfo(accountRepository.save(info));

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
