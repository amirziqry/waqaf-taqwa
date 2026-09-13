package com.taqwa.gowaqaf.modules.donation.project.controller;

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
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationPaymentService;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

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
	private final ProjectDonationReconcileService reconcileService;

	/**
	 * Personal/Merchant only to request payment for project donation.
	 * 
	 * @param authentication
	 * @param projectId
	 * @param request
	 * @return
	 */
	@PostMapping("/{projectId}/donations/payment-request")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(Authentication authentication,
			@PathVariable UUID projectId, @RequestBody ProjectDonationRequest request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

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
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<ProjectDonationDetails> getPaymentStatus(Authentication authentication,
			@PathVariable UUID donationId) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		ProjectDonationDetails response = reconcileService.getDonationDetailsById(principal, donationId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/donations/billing/{billingCode}")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<ProjectDonationDetails> getPaymentStatus(Authentication authentication,
			@PathVariable String billingCode) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		ProjectDonationDetails response = reconcileService.getDonationDetailsByCode(principal, billingCode);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{projectId}/donations/collection")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<ProjectCollectionSum> getProjectCollectionSumById(@PathVariable UUID projectId,
			@ModelAttribute ProjectCollectionSumFilter filter) {
		ProjectCollectionSum response = service.getDonationCollectionByProjectId(projectId, filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
