package com.taqwa.gowaqaf.modules.donation.personal.direct.service.impl;

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
import com.taqwa.gowaqaf.modules.donation.personal.direct.dto.DirectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationPaymentService;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.service.UserService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectDonationPaymentServiceImpl implements DirectDonationPaymentService {

	private final TransactionRepository transactionRepository;
	private final PersonalDonationRepository repository;
	private final UserService userService;
	private final WebhookService webhookService;
	private final PaymentService paymentService;

	@Value("${nexgen.collection.personal}")
	private String collectionCode;

	@Override
	@Transactional
	public PaymentUrlResponse createDonation(CustomUserDetails principal, DirectDonationRequest dto) {
		// Get account.
		Account user = userService.getUserById(principal.getId());
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
		donation.getTransaction().setBillingCode(response.getBillingCode());
		donation.getTransaction().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
	}

	private void validateUser(Account account) {
		String name = account.getAccountHolderName();
		String email = account.getEmail();
		String phone = account.getPhone();

		if (name == null || email == null || phone == null)
			throw new BadRequestException(ErrorCode.USR001, "Please update account name, email, phone.");
	}

	private PersonalDonation buildDonationDetails(Account account, DirectDonationRequest dto, String webhookToken) {
		Transaction transaction = new Transaction();
		PersonalDonation personalDonation = new PersonalDonation();

		// Parent donation
		transaction.setAmount(dto.getAmount());
		transaction.setStatus(PaymentStatus.UNPAID);
		transaction.setDonationType(DonationType.DIRECT);
		transaction.setWebhookToken(webhookToken);

		transaction = transactionRepository.saveAndFlush(transaction);

		// Personal donation
		personalDonation.setId(transaction.getId());
		personalDonation.setTransaction(transaction);
		personalDonation.setAccount(account);

		return repository.saveAndFlush(personalDonation);
	}

	private PaymentRequest buildPaymentRequest(String collectionCode, Account account, PersonalDonation donation,
			String redirectUrl, String callbackUrl) {
		PaymentRequest paymentRequest = new PaymentRequest();

		// Set identity details.
		paymentRequest.setName(account.getAccountHolderName());
		paymentRequest.setEmail(account.getEmail());
		paymentRequest.setPhone(account.getPhone());

		// Set payment details.
		paymentRequest.setAmount(donation.getTransaction().getAmount());
		paymentRequest.setDescription("Personal direct donation.");

		// Set external API details.
		paymentRequest.setCollectionCode(collectionCode);
		paymentRequest.setRedirectUrl(redirectUrl);
		paymentRequest.setCallbackUrl(callbackUrl);

		// Set external ref values.
		paymentRequest.setReferenceLabel1("personalId");
		paymentRequest.setReferenceValue1(account.getId().toString());

		return paymentRequest;
	}

}
