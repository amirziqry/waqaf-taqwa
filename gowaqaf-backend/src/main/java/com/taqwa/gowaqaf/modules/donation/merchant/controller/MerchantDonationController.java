package com.taqwa.gowaqaf.modules.donation.merchant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.donation.merchant.dto.MerchantDonationSum;
import com.taqwa.gowaqaf.modules.donation.merchant.service.MerchantDonationService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.MERCHANT + "/donation")
@RequiredArgsConstructor
public class MerchantDonationController {

	private final MerchantDonationService merchantDonationService;

	@PostMapping("/webhook/payment-update")
	public ResponseEntity<?> webhookPaymentGatewayUpdate() {
		// TODO

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isMerchant(authentication)")
	public ResponseEntity<MerchantDonationSum> getDonationSum(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		MerchantDonationSum response = merchantDonationService.getDonationSumById(principal.getId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
