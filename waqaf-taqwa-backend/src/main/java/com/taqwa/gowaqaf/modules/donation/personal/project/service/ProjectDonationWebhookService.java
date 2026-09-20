package com.taqwa.gowaqaf.modules.donation.personal.project.service;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;

public interface ProjectDonationWebhookService {

	void handleWebhook(String token, NexGenWebhookPayload payload);

}
