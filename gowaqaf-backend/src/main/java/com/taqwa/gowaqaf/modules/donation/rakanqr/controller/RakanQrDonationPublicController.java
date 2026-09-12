package com.taqwa.gowaqaf.modules.donation.rakanqr.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationRequest;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationPaymentService;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationReconcileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/rakan-qr")
@RequiredArgsConstructor
public class RakanQrDonationPublicController {

	private final RakanQrDonationPaymentService paymentService;
	private final RakanQrDonationReconcileService reconcileService;

	/**
	 * FINAL Request payment URL for RakanQr's client.
	 * 
	 * @param agentCode
	 * @param request
	 * @return
	 */
	@PostMapping("/{rakanQrCode}/donations/payment-request")
	public ResponseEntity<PaymentUrlResponse> requestPaymentGatewayUrl(@PathVariable String rakanQrCode,
			@RequestBody RakanQrDonationRequest request) {
		PaymentUrlResponse response = paymentService.createDonation(rakanQrCode, request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * TESTING
	 * 
	 * @param donationId
	 * @return
	 */
	@GetMapping("/donations/{donationId}")
	public ResponseEntity<RakanQrDonationDetails> getDonationDetailsById(@PathVariable UUID donationId) {
		RakanQrDonationDetails response = reconcileService.getDonationDetailsById(donationId);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * TESTING
	 * 
	 * @param billingCode
	 * @return
	 */
	@GetMapping("/donations/billing/{billingCode}")
	public ResponseEntity<RakanQrDonationDetails> getDonationDetailsByCode(@PathVariable String billingCode) {
		RakanQrDonationDetails response = reconcileService.getDonationDetailsByCode(billingCode);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
