package com.taqwa.gowaqaf.external.payment.webhook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;
import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

	private final WebhookService webhookService;
	private final ProjectDonationService projectDonationService;
	private final RakanQrDonationService rakanQrDonationService;

	@PostMapping("/personal/{token}")
	public ResponseEntity<Void> handleWebhookForPersonalDonation(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		webhookService.processWebhook(token, payload.getCode(), payload.getStatus(), payload.getAmount(),
				payload.getPaymentMethodDetail().getTransactionId(), payload.getPaymentMethodDetail().getOrderId(),
				payload.getPaymentMethodDetail().getTransactionDate());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/project/{token}")
	public ResponseEntity<Void> handleWebhookForProjectDonation(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		projectDonationService.processWebhook(token, payload.getCode(), payload.getStatus(), payload.getAmount(),
				payload.getPaymentMethodDetail().getTransactionId(), payload.getPaymentMethodDetail().getOrderId(),
				payload.getPaymentMethodDetail().getTransactionDate());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/rakan-qr/{token}")
	public ResponseEntity<Void> handleWebhookForRakanQrDonation(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		rakanQrDonationService.processWebhook(token, payload.getCode(), payload.getStatus(), payload.getAmount(),
				payload.getPaymentMethodDetail().getTransactionId(), payload.getPaymentMethodDetail().getOrderId(),
				payload.getPaymentMethodDetail().getTransactionDate());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
