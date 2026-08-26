package com.taqwa.gowaqaf.modules.user.personal.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalServiceImpl implements PersonalService {

	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public PersonalRegisterResponse createPersonal(PersonalRegisterCredentials request) {

		Personal user = new Personal();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		// No roles.

		Personal saved = personalRepository.save(user);

		return new PersonalRegisterResponse(saved.getUsername(), saved.getEmail());
	}

}
