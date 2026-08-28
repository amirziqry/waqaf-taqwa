package com.taqwa.gowaqaf.modules.user.personal.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountIdentity;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.mapper.PersonalMapper;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalServiceImpl implements PersonalService {

	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public PersonalRegisterResponse createPersonal(PersonalRegisterCredentials dto) {
		Personal user = new Personal();
		AccountIdentity identity = new AccountIdentity();

		user.setUsername(dto.getUsername());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));

		identity.setEmail(dto.getEmail());
		identity.setPhone(dto.getPhone());
		user.setIdentity(identity);

		Personal saved = personalRepository.save(user);

		return PersonalMapper.mapToRegisterResponse(saved);
	}

	@Override
	public Personal getPersonalByUsername(String username) {
		Personal personal = personalRepository.findByUsername(username).orElseThrow(
				() -> new ResourceNotFoundException(ErrorCode.PER001, String.format("User %s not found.", username)));

		return personal;
	}

}
