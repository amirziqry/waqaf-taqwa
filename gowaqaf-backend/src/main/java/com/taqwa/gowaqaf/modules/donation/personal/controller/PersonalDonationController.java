package com.taqwa.gowaqaf.modules.donation.personal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.PERSONAL + "/donation")
@RequiredArgsConstructor
public class PersonalDonationController {

	private final PersonalDonationService personalDonationService;

	@PostMapping("/payment/request-gateway-url")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(@RequestBody PersonalDonationRequest request) {
		PaymentUrlResponse response = personalDonationService.createDonation(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/webhook/payment-update")
	public ResponseEntity<?> webhookPaymentGatewayUpdate() {
		// TODO

		return new ResponseEntity<>(HttpStatus.OK);
	} 

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PersonalDonationSum> getDonationSum(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		PersonalDonationSum response = personalDonationService.getDonationSumById(principal.getId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
