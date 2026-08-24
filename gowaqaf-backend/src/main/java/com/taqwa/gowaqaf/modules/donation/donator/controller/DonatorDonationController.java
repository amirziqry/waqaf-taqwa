package com.taqwa.gowaqaf.modules.donation.donator.controller;

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

import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationRequest;
import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationSum;
import com.taqwa.gowaqaf.modules.donation.donator.service.DonatorDonationService;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/donator/donation")
@RequiredArgsConstructor
public class DonatorDonationController {

	private final DonatorDonationService donatorDonationService;

	@PostMapping("/payment/request-gateway-url")
	@PreAuthorize("@accountSecurity.isDonator(authentication)")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(@RequestBody DonatorDonationRequest request) {
		PaymentUrlResponse response = donatorDonationService.createDonation(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/webhook/payment-update")
	public ResponseEntity<?> webhookPaymentGatewayUpdate() {
		// TODO

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isDonator(authentication)")
	public ResponseEntity<DonatorDonationSum> getDonationSum(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		DonatorDonationSum response = donatorDonationService.getDonationSumById(principal.getId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
