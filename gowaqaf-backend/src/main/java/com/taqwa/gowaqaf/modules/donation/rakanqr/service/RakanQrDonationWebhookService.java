package com.taqwa.gowaqaf.modules.donation.rakanqr.service;

import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;

public interface RakanQrDonationWebhookService {

	void handleWebhook(String token, NexGenWebhookPayload payload);

}
