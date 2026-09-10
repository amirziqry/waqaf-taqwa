package com.taqwa.gowaqaf.external.payment.webhook.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;

public interface WebhookService {

	String generateWebhookToken();

	String buildWebhookUrl(String donationType, String token);

	void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate);

	Donation getDonationByWebhookToken(String token);

}
