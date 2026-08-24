package com.taqwa.gowaqaf.modules.user.donator.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.user.donator.dto.DonatorRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.donator.dto.DonatorRegisterResponse;
import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;
import com.taqwa.gowaqaf.modules.user.donator.service.DonatorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonatorServiceImpl implements DonatorService {

	private final DonatorRepository donatorRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public DonatorRegisterResponse createDonator(DonatorRegisterCredentials request) {

		Donator user = new Donator();

		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		// No roles.

		Donator saved = donatorRepository.save(user);

		return new DonatorRegisterResponse(saved.getUsername(), saved.getEmail());
	}

}
