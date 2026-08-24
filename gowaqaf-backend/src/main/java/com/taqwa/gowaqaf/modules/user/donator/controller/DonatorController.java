package com.taqwa.gowaqaf.modules.user.donator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.user.donator.dto.DonatorRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.donator.dto.DonatorRegisterResponse;
import com.taqwa.gowaqaf.modules.user.donator.service.DonatorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/donator/")
@RequiredArgsConstructor
public class DonatorController {

	private final DonatorService donatorService;

	@PostMapping("/register")
	public ResponseEntity<DonatorRegisterResponse> register(@RequestBody DonatorRegisterCredentials request) {
		DonatorRegisterResponse response = donatorService.createDonator(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
