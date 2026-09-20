package com.taqwa.gowaqaf.modules.donation.personal.direct.service;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;

public interface DirectDonationWebhookService {

	void handleWebhook(String token, NexGenWebhookPayload payload);

}
