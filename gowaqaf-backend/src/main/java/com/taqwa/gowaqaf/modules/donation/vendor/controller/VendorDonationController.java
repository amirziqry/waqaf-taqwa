package com.taqwa.gowaqaf.modules.donation.vendor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.donation.vendor.dto.VendorDonationSum;
import com.taqwa.gowaqaf.modules.donation.vendor.service.VendorDonationService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor/donation")
@RequiredArgsConstructor
public class VendorDonationController {

	private final VendorDonationService vendorDonationService;

	@PostMapping("/webhook/payment-update")
	public ResponseEntity<?> webhookPaymentGatewayUpdate() {
		// TODO

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isVendor(authentication)")
	public ResponseEntity<VendorDonationSum> getDonationSum(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		VendorDonationSum response = vendorDonationService.getDonationSumById(principal.getId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
