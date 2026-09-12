package com.taqwa.gowaqaf.modules.donation.personal.service;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;

public interface PersonalDonationWebhookService {

	void handleWebhook(String token, NexGenWebhookPayload payload);

}
