package com.taqwa.gowaqaf.modules.user.personal.service.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.admin.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.personal.dto.AccountUploadFields;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.mapper.PersonalMapper;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalServiceImpl implements PersonalService {

	private final PersonalRepository repository;
	private final AccountInfoRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Create.
	 */
	@Override
	@Transactional
	public PersonalRegisterResponse createPersonal(PersonalRegisterCredentials dto) {
		Personal user = new Personal();
		AccountInfo info = new AccountInfo();

		// Set auth credentials.
		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		// Set account details.
		info.setEmail(dto.getEmail());
		info.setPhone(dto.getPhone());
		info.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());
		user.setInfo(accountRepository.save(info));

		Personal saved = repository.save(user);

		return PersonalMapper.mapToRegisterResponse(saved);
	}

	/**
	 * Update personal account details.
	 */
	@Transactional
	@Override
	public void updateAccountByUser(AccountUserDetails principal, AccountUploadFields request) {
		Personal user = getPersonalByUsername(principal.getUsername());

		AccountInfo account = user.getInfo();
		account.setAccountHolderName(request.getAccountHolderName());
		account.setEmail(request.getEmail());
		account.setPhone(request.getPhone());
		account.setModMesra(request.getModMesra() == null ? false : request.getModMesra());

		accountRepository.save(account);
	}

	/**
	 * Provided for other services: Get personal entity by username.
	 */
	@Override
	public Personal getPersonalByUsername(String username) {
		Personal user = repository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PER001, String.format("User %s not found.", username)));

		return user;
	}

	/**
	 * Get personal account info.
	 */
	@Override
	public PersonalAccountInfo getAccountByUser(AccountUserDetails principal) {
		Personal user = getPersonalByUsername(principal.getUsername());

		return PersonalMapper.mapToAccountInfo(user);
	}

	/**
	 * Change password.
	 */
	@Override
	public void changePasswordByUsername(AccountUserDetails principal, ChangePasswordRequest request) {
		Personal user = getPersonalByUsername(principal.getUsername());

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
			throw new BadCredentialsException("Current password is incorrect");

		if (passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
			throw new IllegalArgumentException("New password must be different from current password");

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));

		repository.save(user);
	}

}
