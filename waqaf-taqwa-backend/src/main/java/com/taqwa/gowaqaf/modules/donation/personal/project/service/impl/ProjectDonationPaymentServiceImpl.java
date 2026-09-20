package com.taqwa.gowaqaf.modules.donation.personal.project.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.external.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.external.payment.service.PaymentService;
import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationPaymentService;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.service.UserService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationPaymentServiceImpl implements ProjectDonationPaymentService {

	private final PersonalDonationRepository repository;
	private final TransactionRepository transactionRepository;
	private final UserService userService;
	private final ProjectService projectService;
	private final PaymentService paymentService;
	private final WebhookService webhookService;

	@Value("${nexgen.collection.project}")
	private String collectionCode;

	@Override
	@Transactional
	public PaymentUrlResponse createDonationByProjectId(CustomUserDetails principal, UUID projectId,
			ProjectDonationRequest dto) {
		// Get project
		Project project = projectService.getProjectById(projectId);

		// Get account
		Account user = userService.getUserById(principal.getId());

		// Generate webhook token
		String webhookToken = webhookService.generateWebhookToken();
		String callbackUrl = webhookService.buildWebhookUrl("project", webhookToken);

		// Build and save donation
		PersonalDonation donation = buildDonationDetails(project, user, dto, webhookToken);

		// Build payment request
		PaymentRequest paymentRequest = buildPaymentRequest(collectionCode, user, donation, dto.getRedirectUrl(),
				callbackUrl);

		// Call payment service (Gateway router)
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);
		response.setId(donation.getId());

		// Update donation after billing
		donation.getTransaction().setBillingCode(response.getBillingCode());
		donation.getTransaction().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
	}

	private PersonalDonation buildDonationDetails(Project project, Account account, ProjectDonationRequest dto,
			String webhookToken) {
		Transaction transaction = new Transaction();
		PersonalDonation donation = new PersonalDonation();

		// Parent donation
		transaction.setAmount(dto.getAmount());
		transaction.setStatus(PaymentStatus.UNPAID);
		transaction.setDonationType(DonationType.PROJECT);
		transaction.setWebhookToken(webhookToken);

		transaction = transactionRepository.saveAndFlush(transaction);

		// Project donation
		donation.setId(transaction.getId());
		donation.setTransaction(transaction);
		donation.setProject(project);
		donation.setAccount(account);

		return repository.saveAndFlush(donation);
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
		paymentRequest.setDescription("Project donation.");

		// Set external API details.
		paymentRequest.setCollectionCode(collectionCode);
		paymentRequest.setRedirectUrl(redirectUrl);
		paymentRequest.setCallbackUrl(callbackUrl);

		// Set external ref values.
		paymentRequest.setReferenceLabel1("personalId");
		paymentRequest.setReferenceValue1(account.getId().toString());
		paymentRequest.setReferenceLabel2("projectId");
		paymentRequest.setReferenceValue2(donation.getProject().getId().toString());

		return paymentRequest;
	}
}
