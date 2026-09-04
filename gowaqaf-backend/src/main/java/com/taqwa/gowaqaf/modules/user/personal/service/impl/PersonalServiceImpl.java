package com.taqwa.gowaqaf.modules.user.personal.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
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

	private final PersonalRepository personalRepository;
	private final AccountInfoRepository infoRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 
	 */
	@Override
	public PersonalRegisterResponse createPersonal(PersonalRegisterCredentials dto) {
		Personal user = new Personal();
		AccountInfo info = new AccountInfo();

		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		info.setEmail(dto.getEmail());
		info.setPhone(dto.getPhone());
		info.setModMesra(dto.getModMesra() == null ? false : dto.getModMesra());
		user.setInfo(infoRepository.save(info));

		Personal saved = personalRepository.save(user);

		return PersonalMapper.mapToRegisterResponse(saved);
	}

	/**
	 * Provided for other services: Get personal entity by username.
	 */
	@Override
	public Personal getPersonalByUsername(String username) {
		Personal personal = personalRepository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PER001, String.format("User %s not found.", username)));

		return personal;
	}

	/**
	 * Update personal account details.
	 */
	@Transactional
	@Override
	public void updateAccountByUser(AccountUserDetails principal, AccountUploadFields request) {
		Personal personal = personalRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PER001,
						String.format("User %s not found.", principal.getUsername())));

		AccountInfo profile = personal.getInfo();
		profile.setAccountHolderName(request.getAccountHolderName());
		profile.setEmail(request.getEmail());
		profile.setPhone(request.getPhone());
		profile.setModMesra(request.getModMesra() == null ? false : request.getModMesra());

		infoRepository.save(profile);
	}

	/**
	 * Get personal account info.
	 */
	@Override
	public PersonalAccountInfo getAccountByUser(AccountUserDetails principal) {
		Personal personal = personalRepository.findByUsername(principal.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PER001,
						String.format("User %s not found.", principal.getUsername())));

		return PersonalMapper.mapToAccountInfo(personal);
	}

}
