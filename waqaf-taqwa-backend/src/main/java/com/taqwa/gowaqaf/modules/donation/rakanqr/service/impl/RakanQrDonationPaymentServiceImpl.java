package com.taqwa.gowaqaf.modules.donation.rakanqr.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.external.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.external.payment.service.PaymentService;
import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationRequest;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationPaymentService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationPaymentServiceImpl implements RakanQrDonationPaymentService {

	private final TransactionRepository transactionRepository;
	private final RakanQrDonationRepository rakanQrDonationRepository;
	private final RakanQrService rakanQrService;
	private final WebhookService webhookService;
	private final PaymentService paymentService;

	@Value("${nexgen.collection.rakanqr}")
	private String collectionCode;

	@Override
	@Transactional
	public PaymentUrlResponse createDonation(String agentCode, RakanQrDonationRequest request) {
		RakanQr agent = rakanQrService.getRakanQrByUser(agentCode);

		// Check rakan qr validity.
		if (agent.getStatus() != RakanQrStatus.ACTIVE && agent.getType() != RakanQrType.DUTA)
			throw new BadRequestException(ErrorCode.RQA002, "Service not available");

		// Generate webhook token.
		String webhookToken = webhookService.generateWebhookToken();
		String callbackUrl = webhookService.buildWebhookUrl("rakan-qr", webhookToken);

		// Build donation object.
		RakanQrDonation donation = buildDonationDetails(agent, request, webhookToken);

		// Build payment request dto.
		PaymentRequest paymentRequest = buildPaymentRequest(agent.getCollectionCode(), agent, donation,
				request.getRedirectUrl(), callbackUrl);

		// Call payment service (Gateway router).
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);
		response.setId(donation.getId());

		// Update donation object.
		donation.getTransaction().setBillingCode(response.getBillingCode());
		donation.getTransaction().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
	}

	private RakanQrDonation buildDonationDetails(RakanQr rakanQr, RakanQrDonationRequest dto, String webhookToken) {
		Transaction transaction = new Transaction();
		RakanQrDonation rakanQrDonation = new RakanQrDonation();

		// Parent donation
		transaction.setAmount(dto.getAmount());
		transaction.setStatus(PaymentStatus.UNPAID);
		transaction.setDonationType(DonationType.RAKANQR);
		transaction.setWebhookToken(webhookToken);

		transaction = transactionRepository.saveAndFlush(transaction);

		// RakanQr donation
		rakanQrDonation.setId(transaction.getId());
		rakanQrDonation.setTransaction(transaction);
		rakanQrDonation.setRakanQr(rakanQr);

		RakanQrDonation saved = rakanQrDonationRepository.saveAndFlush(rakanQrDonation);

		return saved;
	}

	private PaymentRequest buildPaymentRequest(String collectionCode, RakanQr rakanQr, RakanQrDonation donation,
			String redirectUrl, String callbackUrl) {
		PaymentRequest paymentRequest = new PaymentRequest();

		// Set identity details.
		paymentRequest.setName(rakanQr.getFullName());
		paymentRequest.setEmail(rakanQr.getAccount().getEmail());
		paymentRequest.setPhone(rakanQr.getPhone());

		// Set payment details.
		paymentRequest.setAmount(donation.getTransaction().getAmount());
		paymentRequest.setDescription("RakanQr client donation.");

		// Set external API details.
		paymentRequest.setCollectionCode(collectionCode);
		paymentRequest.setRedirectUrl(redirectUrl);
		paymentRequest.setCallbackUrl(callbackUrl);

		// Set external ref values.
		paymentRequest.setReferenceLabel1("rakanQrId");
		paymentRequest.setReferenceValue1(rakanQr.getId().toString());

		return paymentRequest;
	}

}
