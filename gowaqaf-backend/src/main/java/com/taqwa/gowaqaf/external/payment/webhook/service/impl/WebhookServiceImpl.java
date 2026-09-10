package com.taqwa.gowaqaf.external.payment.webhook.service.impl;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

	private final DonationRepository donationRepository;
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

	@Override
	public Donation getDonationByWebhookToken(String token) {
		Donation donation = donationRepository.findByWebhookToken(token)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

		return donation;
	}

	@Transactional
	@Override
	public void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate) {
		Donation donation = getDonationByWebhookToken(token);

		if (!donation.getBillingCode().equals(code))
			throw new BadRequestException(ErrorCode.WHK002, "Billing code does not match");

		if (donation.getAmount().compareTo(amount) != 0)
			throw new BadRequestException(ErrorCode.WHK003, "Amount does not match");

		switch (status.toLowerCase()) {
		case "paid" -> donation.setStatus(PaymentStatus.PAID);

		case "pending" -> donation.setStatus(PaymentStatus.PENDING);

		case "unpaid" -> donation.setStatus(PaymentStatus.UNPAID);

		default -> donation.setStatus(PaymentStatus.EXPIRED);
		}

		if (donation.getStatus() == PaymentStatus.PAID) {
			donation.setTransactionId(transactionId != null ? transactionId : orderId);
			donation.setPaidAt(transactionDate);
			donation.setWebhookToken(null);
		}
	}

}
