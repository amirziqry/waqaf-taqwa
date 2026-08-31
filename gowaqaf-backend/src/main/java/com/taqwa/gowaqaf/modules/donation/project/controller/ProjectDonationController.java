package com.taqwa.gowaqaf.modules.donation.project.controller;

import java.util.UUID;

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

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/project/donation")
@RequiredArgsConstructor
public class ProjectDonationController {

	private final ProjectDonationService donationService;

	@PostMapping("/{projectId}/payment/request-gateway-url")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(Authentication authentication,
			@PathVariable UUID projectId, @RequestBody PersonalDonationRequest request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		PaymentUrlResponse response = donationService.createDonationByProjectId(principal, projectId, request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/{projectId}/payment/webhook/payment-update")
	public ResponseEntity<?> webhookPaymentGatewayUpdate(@PathVariable UUID projectId) {
		// TODO

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/payment/{id}/status")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<ProjectDonationDetails> getPaymentStatus(Authentication authentication,
			@PathVariable UUID id) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		ProjectDonationDetails response = donationService.getPaymentStatus(principal.getId(), id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{projectId}/collection")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<ProjectCollectionSum> getProjectCollectionSumById(@PathVariable UUID projectId,
			@ModelAttribute ProjectCollectionSumFilter filter) {
		ProjectCollectionSum response = donationService.getProjectCollectionSumById(projectId, filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
