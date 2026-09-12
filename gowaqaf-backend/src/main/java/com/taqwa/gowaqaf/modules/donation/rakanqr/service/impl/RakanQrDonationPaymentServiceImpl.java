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
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrAccount;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationPaymentServiceImpl implements RakanQrDonationPaymentService {

	private final DonationRepository donationRepository;
	private final RakanQrDonationRepository rakanQrDonationRepository;
	private final RakanQrService rakanQrService;
	private final WebhookService webhookService;
	private final PaymentService paymentService;

	@Value("${nexgen.collection.rakanqr}")
	private String collectionCode;

	@Transactional
	@Override
	public PaymentUrlResponse createDonation(String agentCode, RakanQrDonationRequest request) {
		RakanQr agent = rakanQrService.getRakanQrByUser(agentCode);

		// Check rakan qr validity.
		if (agent.getStatus() != RakanQrStatus.ACTIVE)
			throw new BadRequestException(ErrorCode.RQA002, "Invalid RakanQr code.");

		// Generate webhook token.
		String webhookToken = webhookService.generateWebhookToken();
		String callbackUrl = webhookService.buildWebhookUrl("rakan-qr", webhookToken);

		// Build donation object.
		RakanQrDonation donation = buildDonationDetails(agent, request, webhookToken);

		// Build payment request dto.
		PaymentRequest paymentRequest = buildPaymentRequest(collectionCode, agent, donation, request.getRedirectUrl(),
				callbackUrl);

		// Call payment service (Gateway router).
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);
		response.setId(donation.getId());

		// Update donation object.
		donation.getDonation().setBillingCode(response.getBillingCode());
		donation.getDonation().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
	}

	private RakanQrDonation buildDonationDetails(RakanQr rakanQr, RakanQrDonationRequest dto, String webhookToken) {
		Donation donation = new Donation();
		RakanQrDonation rakanQrDonation = new RakanQrDonation();

		// Parent donation
		donation.setAmount(dto.getAmount());
		donation.setStatus(PaymentStatus.UNPAID);
		donation.setDonationType(DonationType.RAKANQR);
		donation.setWebhookToken(webhookToken);

		donation = donationRepository.saveAndFlush(donation);

		// RakanQr donation
		rakanQrDonation.setId(donation.getId());
		rakanQrDonation.setDonation(donation);
		rakanQrDonation.setRakanQr(rakanQr);

		RakanQrDonation saved = rakanQrDonationRepository.saveAndFlush(rakanQrDonation);

		return saved;
	}

	private PaymentRequest buildPaymentRequest(String collectionCode, RakanQr rakanQr, RakanQrDonation donation,
			String redirectUrl, String callbackUrl) {
		String accountHolderName = null, email = null, phone = null;

		if (rakanQr.getAccount() == RakanQrAccount.PERSONAL) {
			accountHolderName = rakanQr.getPersonal().getInfo().getAccountHolderName();
			email = rakanQr.getPersonal().getInfo().getEmail();
			phone = rakanQr.getPersonal().getInfo().getPhone();
		}

		if (rakanQr.getAccount() == RakanQrAccount.MERCHANT) {
			accountHolderName = rakanQr.getMerchant().getInfo().getAccountHolderName();
			email = rakanQr.getMerchant().getInfo().getEmail();
			phone = rakanQr.getMerchant().getInfo().getPhone();
		}

		PaymentRequest paymentRequest = new PaymentRequest();

		// Set identity details.
		paymentRequest.setName(accountHolderName);
		paymentRequest.setEmail(email);
		paymentRequest.setPhone(phone);

		// Set payment details.
		paymentRequest.setAmount(donation.getDonation().getAmount());
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
