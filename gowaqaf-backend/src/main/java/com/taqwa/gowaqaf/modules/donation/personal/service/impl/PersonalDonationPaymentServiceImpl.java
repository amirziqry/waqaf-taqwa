package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

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
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationPaymentService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDonationPaymentServiceImpl implements PersonalDonationPaymentService {

	private final DonationRepository donationRepository;
	private final PersonalDonationRepository repository;
	private final PersonalService userService;
	private final WebhookService webhookService;
	private final PaymentService paymentService;

	@Value("${nexgen.collection.personal}")
	private String collectionCode;

	@Override
	@Transactional
	public PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto) {
		// Get account.
		Personal user = userService.getPersonalByUsername(principal.getUsername());
		validateUser(user);

		// Generate webhook token.
		String webhookToken = webhookService.generateWebhookToken();
		String callbackUrl = webhookService.buildWebhookUrl("personal", webhookToken);

		// Build donation object.
		PersonalDonation donation = buildDonationDetails(user, dto, webhookToken);

		// Build payment request dto.
		PaymentRequest paymentRequest = buildPaymentRequest(collectionCode, user, donation, dto.getRedirectUrl(),
				callbackUrl);

		// Call payment service (Gateway router).
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);
		response.setId(donation.getId());

		// Update donation object.
		donation.getDonation().setBillingCode(response.getBillingCode());
		donation.getDonation().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
	}

	private void validateUser(Personal personal) {
		String name = personal.getInfo().getAccountHolderName();
		String email = personal.getInfo().getEmail();
		String phone = personal.getInfo().getPhone();

		if (name == null || email == null || phone == null)
			throw new BadRequestException(ErrorCode.PER001, "Please update account name, email, phone.");
	}

	private PersonalDonation buildDonationDetails(Personal personal, PersonalDonationRequest dto, String webhookToken) {
		Donation donation = new Donation();
		PersonalDonation personalDonation = new PersonalDonation();

		// Parent donation
		donation.setAmount(dto.getAmount());
		donation.setStatus(PaymentStatus.UNPAID);
		donation.setDonationType(DonationType.DIRECT);
		donation.setWebhookToken(webhookToken);

		donation = donationRepository.saveAndFlush(donation);

		// Personal donation
		personalDonation.setId(donation.getId());
		personalDonation.setDonation(donation);
		personalDonation.setPersonal(personal);
		personalDonation.setTaxExempt(dto.getTaxExempt());

		return repository.saveAndFlush(personalDonation);
	}

	private PaymentRequest buildPaymentRequest(String collectionCode, Personal personal, PersonalDonation donation,
			String redirectUrl, String callbackUrl) {
		PaymentRequest paymentRequest = new PaymentRequest();

		// Set identity details.
		paymentRequest.setName(personal.getInfo().getAccountHolderName());
		paymentRequest.setEmail(personal.getInfo().getEmail());
		paymentRequest.setPhone(personal.getInfo().getPhone());

		// Set payment details.
		paymentRequest.setAmount(donation.getDonation().getAmount());
		paymentRequest.setDescription("Personal direct donation.");

		// Set external API details.
		paymentRequest.setCollectionCode(collectionCode);
		paymentRequest.setRedirectUrl(redirectUrl);
		paymentRequest.setCallbackUrl(callbackUrl);

		// Set external ref values.
		paymentRequest.setReferenceLabel1("personalId");
		paymentRequest.setReferenceValue1(personal.getId().toString());

		return paymentRequest;
	}

}
