package com.taqwa.gowaqaf.modules.donation.personal.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.USER + "/donations")
@RequiredArgsConstructor
public class PersonalDonationController {

	private final PersonalDonationService service;

	@GetMapping("/contributions")
	public ResponseEntity<PersonalDonationSum> getDonationSum(Authentication authentication,
			@ModelAttribute PersonalDonationSumFilter filter) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PersonalDonationSum response = service.getDonationSumByUser(principal.getId(), filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Page<PersonalDonationDetails>> getAllDonationDetailsByUser(Authentication authentication,
			@PageableDefault(size = 10, sort = "transaction.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		Page<PersonalDonationDetails> response = service.getAllDonationDetailsByUser(principal.getId(), pageable);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
