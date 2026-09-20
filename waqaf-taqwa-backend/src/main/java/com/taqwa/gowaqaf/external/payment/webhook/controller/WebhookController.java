package com.taqwa.gowaqaf.external.payment.webhook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationWebhookService;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationWebhookService;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationWebhookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

	private final DirectDonationWebhookService personalWebhookService;
	private final ProjectDonationWebhookService projectWebhookService;
	private final RakanQrDonationWebhookService rakanQrWebhookService;

	@PostMapping("/personal/{token}")
	public ResponseEntity<Void> handleWebhookForPersonalDonation(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		personalWebhookService.handleWebhook(token, payload);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/project/{token}")
	public ResponseEntity<Void> handleWebhookForProjectDonation(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		projectWebhookService.handleWebhook(token, payload);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/rakan-qr/{token}")
	public ResponseEntity<Void> handleWebhookForRakanQrDonation(@PathVariable String token,
			@RequestBody NexGenWebhookPayload payload) {
		rakanQrWebhookService.handleWebhook(token, payload);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
