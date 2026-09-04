package com.taqwa.gowaqaf.external.payment.webhook.service;

public interface WebhookService {

	String generateWebhookToken();

	String buildWebhookUrl(String donationType, String token);

}
