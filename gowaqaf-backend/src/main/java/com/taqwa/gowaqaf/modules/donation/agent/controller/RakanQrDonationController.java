package com.taqwa.gowaqaf.modules.donation.agent.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationSum;
import com.taqwa.gowaqaf.modules.donation.agent.service.RakanQrDonationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rakan-qr-agent")
@RequiredArgsConstructor
public class RakanQrDonationController {

	private final RakanQrDonationService service;

	@PostMapping("/webhook")
	public ResponseEntity<Void> handleWebhook() {

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/donation/sum")
	public ResponseEntity<RakanQrDonationSum> getRakanQrDonationSum(Authentication authentication,
			@ModelAttribute RakanQrDonationFilter filter) {
		RakanQrDonationSum response = service.getDonationSumByUser(authentication, filter);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
