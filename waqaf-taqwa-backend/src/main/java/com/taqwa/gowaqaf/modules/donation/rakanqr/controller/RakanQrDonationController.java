package com.taqwa.gowaqaf.modules.donation.rakanqr.controller;

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
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.RAKANQR + "/donations")
@RequiredArgsConstructor
public class RakanQrDonationController {

	private final RakanQrDonationService service;

	/**
	 * FINAL RakanQr-only to retrieve respective collected donations.
	 * 
	 * @param authentication
	 * @param filter
	 * @return
	 */
	@GetMapping("/collection")
	public ResponseEntity<RakanQrCollection> getRakanQrDonationCollectionByUser(Authentication authentication,
			@ModelAttribute RakanQrDonationFilter filter) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		RakanQrCollection response = service.getDonationCollectionByUser(principal, filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * FINAL Get donation list By RakanQr.
	 * 
	 * @param authentication
	 * @param pageable
	 * @return
	 */
	@GetMapping
	public ResponseEntity<Page<RakanQrDonationDetails>> getAllDonationDetailsByUser(Authentication authentication,
			@PageableDefault(size = 10, sort = "transaction.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		Page<RakanQrDonationDetails> response = service.getAllDonationDetailsByUser(principal, pageable);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
