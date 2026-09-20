package com.taqwa.gowaqaf.modules.donation.personal.direct.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.direct.dto.DirectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationPaymentService;
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationService;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.USER + "/donations")
@RequiredArgsConstructor
public class DirectDonationController {

	private final DirectDonationService service;
	private final DirectDonationPaymentService paymentService;

	@PostMapping("/payment-request")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(Authentication authentication,
			@RequestBody DirectDonationRequest request) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PaymentUrlResponse response = paymentService.createDonation(principal, request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PersonalDonationDetails> getDonationDetailsById(Authentication authentication,
			@PathVariable UUID id) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PersonalDonationDetails response = service.getDonationDetailsById(principal.getId(), id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/billing/{code}")
	public ResponseEntity<PersonalDonationDetails> getDonationDetailsById(Authentication authentication,
			@PathVariable String code) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PersonalDonationDetails response = service.getDonationDetailsByCode(principal.getId(), code);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
