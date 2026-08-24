package com.taqwa.gowaqaf.module.payment;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationSum;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/project/donation")
@RequiredArgsConstructor
public class ProjectDonationController {

	private final ProjectDonationService projectDonationService;

	@PostMapping("/{projectId}/payment/request-gateway-url")
	@PreAuthorize("@accountSecurity.isDonator(authentication)")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(@PathVariable UUID projectId,
			@RequestBody ProjectDonationRequest request) {
		PaymentUrlResponse response = projectDonationService.createDonationByProjectId(projectId, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/webhook/payment-update")
	public ResponseEntity<?> webhookPaymentGatewayUpdate() {
		// TODO

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{projectId}/sum")
	@PreAuthorize("@accountSecurity.isMember(authentication) && hasRole('ADMIN')")
	public ResponseEntity<ProjectDonationSum> getProjectDonationSumById(@PathVariable UUID projectId) {
		ProjectDonationSum response = projectDonationService.getProjectDonationSumById(projectId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
