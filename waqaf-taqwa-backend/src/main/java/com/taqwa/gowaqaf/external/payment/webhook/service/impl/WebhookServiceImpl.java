package com.taqwa.gowaqaf.external.payment.webhook.service.impl;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

	private final SecureRandom secureRandom = new SecureRandom();

	@Value("${webhook.base-url}")
	private String webhookBaseUrl;

	@Override
	public String generateWebhookToken() {
		byte[] bytes = new byte[32];
		secureRandom.nextBytes(bytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	@Override
	public String buildWebhookUrl(String donationType, String token) {
		return webhookBaseUrl + "/api/webhook/" + donationType + "/" + token;
	}

}
