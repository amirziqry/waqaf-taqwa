package com.taqwa.gowaqaf.modules.user.merchant.service.impl;

import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.admin.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantAccountInfo;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.mapper.MerchantMapper;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;
import com.taqwa.gowaqaf.modules.user.personal.dto.AccountUploadFields;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

	private final MerchantRepository repository;
	private final AccountInfoRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Create.
	 */
	@Override
	@Transactional
	public MerchantRegisterResponse createMerchant(MerchantRegisterCredentials dto) {
		Merchant user = new Merchant();
		AccountInfo account = new AccountInfo();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		// Set account details.
		account.setEmail(dto.getEmail());
		account.setPhone(dto.getPhone());
		account.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());
		user.setInfo(accountRepository.save(account));

		Merchant saved = repository.save(user);

		return MerchantMapper.mapToRegisterResponse(saved);
	}

	@Override
	public void updateAccountByUser(AccountUserDetails principal, AccountUploadFields request) {
		Merchant user = getMerchantByUsername(principal.getUsername());

		AccountInfo account = user.getInfo();
		account.setAccountHolderName(request.getAccountHolderName());
		account.setEmail(request.getEmail());
		account.setPhone(request.getPhone());
		account.setModMesra(request.getModMesra() == null ? false : request.getModMesra());

		accountRepository.save(account);
	}

	@Override
	public Merchant getMerchantById(UUID userId) {
		Merchant user = repository.findById(userId).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MER001, String.format("User %s not found.", userId)));

		return user;
	}

	/**
	 * Provided for other services: Get user entity by username.
	 */
	@Override
	public Merchant getMerchantByUsername(String username) {
		Merchant user = repository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.MER001, String.format("User %s not found.", username)));

		return user;
	}

	/**
	 * Get personal account info.
	 */
	@Override
	public MerchantAccountInfo getAccountByUser(AccountUserDetails principal) {
		Merchant user = getMerchantByUsername(principal.getUsername());

		return MerchantMapper.mapToAccountInfo(user);
	}

	/**
	 * Change password.
	 */
	@Override
	public void changePasswordByUsername(AccountUserDetails principal, ChangePasswordRequest request) {
		Merchant user = getMerchantByUsername(principal.getUsername());

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
			throw new BadCredentialsException("Current password is incorrect");

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
			throw new IllegalArgumentException("New password must be different from current password");

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		repository.save(user);
	}

}
