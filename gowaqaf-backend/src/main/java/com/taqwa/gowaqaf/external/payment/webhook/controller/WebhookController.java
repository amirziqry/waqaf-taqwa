package com.taqwa.gowaqaf.external.payment.webhook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

	private final PersonalDonationService personalDonationService;

	@PostMapping("/personal/{token}")
	public ResponseEntity<Void> handleWebhookForPersonal(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		personalDonationService.processWebhook(token, payload.getCode(), payload.getStatus(), payload.getAmount(),
				payload.getPaymentMethodDetail().getTransactionId(), payload.getPaymentMethodDetail().getOrderId(),
				payload.getPaymentMethodDetail().getTransactionDate());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
