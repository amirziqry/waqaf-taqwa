package com.taqwa.gowaqaf.modules.donation.personal.project.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationAdminService;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationPaymentService;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Project donation management handler
 * <p>
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectDonationController {

	private final ProjectDonationService service;
	private final ProjectDonationPaymentService paymentService;
	private final ProjectDonationAdminService adminService;

	/**
	 * Personal/Merchant only to request payment for project donation.
	 * 
	 * @param authentication
	 * @param projectId
	 * @param request
	 * @return
	 */
	@PostMapping("/{projectId}/donations/payment-request")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(Authentication authentication,
			@PathVariable UUID projectId, @RequestBody ProjectDonationRequest request) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PaymentUrlResponse response = paymentService.createDonationByProjectId(principal, projectId, request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * Personal/Merchant only to request payment status for the respective project
	 * donation.
	 * 
	 * @param authentication
	 * @param donationId
	 * @return
	 */
	@GetMapping("/donations/{donationId}")
	public ResponseEntity<PersonalDonationDetails> getPaymentStatus(Authentication authentication,
			@PathVariable UUID donationId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PersonalDonationDetails response = service.getDonationDetailsById(principal.getId(), donationId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/donations/billing/{billingCode}")
	public ResponseEntity<PersonalDonationDetails> getPaymentStatus(Authentication authentication,
			@PathVariable String billingCode) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PersonalDonationDetails response = service.getDonationDetailsByCode(principal.getId(), billingCode);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{projectId}/donations/collection")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ProjectCollectionSum> getProjectCollectionSumById(@PathVariable UUID projectId,
			@ModelAttribute ProjectCollectionSumFilter filter) {
		ProjectCollectionSum response = adminService.getCollectedAmountByProjectId(projectId, filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
