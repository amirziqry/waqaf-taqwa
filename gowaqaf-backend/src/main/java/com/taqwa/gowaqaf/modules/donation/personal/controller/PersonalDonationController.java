package com.taqwa.gowaqaf.modules.donation.personal.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.PERSONAL + "/donation")
@RequiredArgsConstructor
public class PersonalDonationController {

	private final PersonalDonationService donationService;

	@PostMapping("/payment/request-gateway-url")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(Authentication authentication,
			@RequestBody PersonalDonationRequest request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		PaymentUrlResponse response = donationService.createDonation(principal, request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/payment/{id}/status")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PersonalDonationDetails> getPaymentStatus(Authentication authentication,
			@PathVariable UUID id) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		PersonalDonationDetails response = donationService.getPaymentStatus(id, principal.getId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PersonalDonationSum> getDonationSum(Authentication authentication,
			@ModelAttribute PersonalDonationSumFilter filter) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		PersonalDonationSum response = donationService.getDonationSumByUser(principal.getId(), filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/get/all")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<Page<PersonalDonationDetails>> getAllDonationDetailsByUser(Authentication authentication,
			@PageableDefault(size = 10, sort = "paidAt", direction = Sort.Direction.DESC) Pageable pageable) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		Page<PersonalDonationDetails> response = donationService.getAllDonationDetailsByUser(principal.getId(),
				pageable);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
