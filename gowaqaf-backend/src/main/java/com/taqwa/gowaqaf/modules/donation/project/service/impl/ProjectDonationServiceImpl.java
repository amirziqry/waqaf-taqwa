package com.taqwa.gowaqaf.modules.donation.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.external.payment.service.PaymentService;
import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;
import com.taqwa.gowaqaf.modules.donation.project.mapper.ProjectDonationMapper;
import com.taqwa.gowaqaf.modules.donation.project.repository.ProjectDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationServiceImpl implements ProjectDonationService {

	private final DonationRepository donationRepository;
	private final ProjectDonationRepository projectDonationRepository;
	private final PersonalService userService;
	private final MerchantService merchantService;
	private final ProjectService projectService;
	private final PaymentService paymentService;
	private final WebhookService webhookService;

	@Value("${nexgen.collection.project}")
	private String collectionCode;

	@Transactional
	@Override
	public PaymentUrlResponse createDonationByProjectId(AccountUserDetails principal, UUID projectId,
			ProjectDonationRequest dto) {
		// Get project
		Project project = projectService.getProjectById(projectId);

		// Get account
		Personal personal = null;
		Merchant merchant = null;

		if (principal.getAccountType() == AccountType.PERSONAL)
			personal = userService.getPersonalByUsername(principal.getUsername());

		if (principal.getAccountType() == AccountType.MERCHANT)
			merchant = merchantService.getMerchantByUsername(principal.getUsername());

		// Generate webhook token
		String webhookToken = webhookService.generateWebhookToken();
		String callbackUrl = webhookService.buildWebhookUrl("project", webhookToken);

		// Build and save donation
		ProjectDonation donation = buildDonationDetails(project, personal, merchant, dto, webhookToken);

		// Build payment request
		PaymentRequest paymentRequest = buildPaymentRequest(collectionCode, personal, merchant, donation,
				dto.getRedirectUrl(), callbackUrl);

		// Call payment service (Gateway router)
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);
		response.setId(donation.getId());

		// Update donation after billing
		donation.getDonation().setBillingCode(response.getBillingCode());
		donation.getDonation().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
	}

	private ProjectDonation buildDonationDetails(Project project, Personal personal, Merchant merchant,
			ProjectDonationRequest dto, String webhookToken) {
		Donation donation = new Donation();
		ProjectDonation projectDonation = new ProjectDonation();

		// Parent donation
		donation.setAmount(dto.getAmount());
		donation.setStatus(PaymentStatus.UNPAID);
		donation.setDonationType(DonationType.PROJECT);
		donation.setWebhookToken(webhookToken);

		donation = donationRepository.saveAndFlush(donation);

		// Project donation
		projectDonation.setId(donation.getId());
		projectDonation.setDonation(donation);
		projectDonation.setProject(project);
		projectDonation.setPersonal(personal);
		projectDonation.setMerchant(merchant);
		projectDonation.setTaxExempt(dto.getTaxExempt());

		return projectDonationRepository.saveAndFlush(projectDonation);
	}

	private PaymentRequest buildPaymentRequest(String collectionCode, Personal personal, Merchant merchant,
			ProjectDonation donation, String redirectUrl, String callbackUrl) {

		PaymentRequest paymentRequest = new PaymentRequest();

		// Set identity details.
		if (personal != null) {
			paymentRequest.setName(personal.getInfo().getAccountHolderName());
			paymentRequest.setEmail(personal.getInfo().getEmail());
			paymentRequest.setPhone(personal.getInfo().getPhone());
		}

		if (merchant != null) {
			paymentRequest.setName(merchant.getInfo().getAccountHolderName());
			paymentRequest.setEmail(merchant.getInfo().getEmail());
			paymentRequest.setPhone(merchant.getInfo().getPhone());
		}

		// Set payment details.
		paymentRequest.setAmount(donation.getDonation().getAmount());
		paymentRequest.setDescription("Project donation.");

		// Set external API details.
		paymentRequest.setCollectionCode(collectionCode);
		paymentRequest.setRedirectUrl(redirectUrl);
		paymentRequest.setCallbackUrl(callbackUrl);

		return paymentRequest;
	}

	@Override
	public ProjectDonationDetails getPaymentStatus(AccountUserDetails principal, UUID donationId) {
		ProjectDonation donation = null;

		if (principal.getAccountType() == AccountType.PERSONAL)
			donation = projectDonationRepository.findByIdAndPersonalId(donationId, principal.getId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		if (principal.getAccountType() == AccountType.MERCHANT)
			donation = projectDonationRepository.findByIdAndMerchantId(donationId, principal.getId())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		return ProjectDonationMapper.mapToDetails(donation);
	}

	@Override
	public ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, ProjectCollectionSumFilter filter) {
		return getDonationCollectionByProjectId(projectId, filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, LocalDate startDate,
			LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = projectDonationRepository.sumPaidDonationsByProjectId(projectId, startDateTime, endDateTime);

		return new ProjectCollectionSum(total);
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

	@Transactional
	@Override
	public void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate) {
		ProjectDonation donation = projectDonationRepository.findByDonation_WebhookToken(token)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

		if (!donation.getDonation().getBillingCode().equals(code))
			throw new BadRequestException(ErrorCode.WHK002, "Billing code does not match");

		if (donation.getDonation().getAmount().compareTo(amount) != 0)
			throw new BadRequestException(ErrorCode.WHK003, "Amount does not match");

		PaymentStatus previousStatus = donation.getDonation().getStatus();

		switch (status.toLowerCase()) {
		case "paid" -> donation.getDonation().setStatus(PaymentStatus.PAID);
		case "pending" -> donation.getDonation().setStatus(PaymentStatus.PENDING);
		case "unpaid" -> donation.getDonation().setStatus(PaymentStatus.UNPAID);
		default -> donation.getDonation().setStatus(PaymentStatus.EXPIRED);
		}

		if (donation.getDonation().getStatus() == PaymentStatus.PAID && previousStatus != PaymentStatus.PAID) {
			donation.getDonation().setTransactionId(transactionId != null ? transactionId : orderId);
			donation.getDonation().setPaidAt(transactionDate);
			donation.getDonation().setWebhookToken(null);

			if (previousStatus != PaymentStatus.PAID)
				projectService.updateProjectCollectedAmountById(donation.getProject().getId(),
						donation.getDonation().getAmount());

		}
	}

}
