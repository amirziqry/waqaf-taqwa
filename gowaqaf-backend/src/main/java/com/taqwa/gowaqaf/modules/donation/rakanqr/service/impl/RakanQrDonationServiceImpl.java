package com.taqwa.gowaqaf.modules.donation.rakanqr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationRequest;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.mapper.RakanQrDonationMapper;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrAccount;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationServiceImpl implements RakanQrDonationService {

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

	private RakanQrDonation buildDonationDetails(RakanQr agent, RakanQrDonationRequest dto, String webhookToken) {
		Donation donation = new Donation();
		RakanQrDonation rakanQrDonation = new RakanQrDonation();

		// Parent donation
		donation.setAmount(dto.getAmount());
		donation.setStatus(PaymentStatus.UNPAID);
		donation.setDonationType(DonationType.PROJECT);
		donation.setWebhookToken(webhookToken);

		donation = donationRepository.saveAndFlush(donation);

		// RakanQr donation
		rakanQrDonation.setId(donation.getId());
		rakanQrDonation.setDonation(donation);
		rakanQrDonation.setRakanQr(agent);

		RakanQrDonation saved = rakanQrDonationRepository.saveAndFlush(rakanQrDonation);

		return saved;
	}

	private PaymentRequest buildPaymentRequest(String collectionCode, RakanQr agent, RakanQrDonation donation,
			String redirectUrl, String callbackUrl) {
		String accountHolderName = null, email = null, phone = null;

		if (agent.getAccount() == RakanQrAccount.PERSONAL) {
			accountHolderName = agent.getPersonal().getInfo().getAccountHolderName();
			email = agent.getPersonal().getInfo().getEmail();
			phone = agent.getPersonal().getInfo().getPhone();
		}

		if (agent.getAccount() == RakanQrAccount.MERCHANT) {
			accountHolderName = agent.getMerchant().getInfo().getAccountHolderName();
			email = agent.getMerchant().getInfo().getEmail();
			phone = agent.getMerchant().getInfo().getPhone();
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

		return paymentRequest;
	}

	@Override
	public RakanQrDonationDetails getDonationDetailsById(UUID donationId) {
		RakanQrDonation donation = rakanQrDonationRepository.findById(donationId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RQD001, "Donation not found"));

		return RakanQrDonationMapper.mapToDetails(donation);
	}

	@Override
	public RakanQrCollection getDonationCollectionByUser(AccountUserDetails principal, RakanQrDonationFilter filter) {
		RakanQr agent = rakanQrService.getRakanQrByUser(principal);

		RakanQrCollection sum = getDonationCollectionByUser(agent.getId(), filter.getStartDate(), filter.getEndDate());

		return sum;
	}

	@Override
	public RakanQrCollection getDonationCollectionByUser(UUID rakanQrId, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal sum = rakanQrDonationRepository.sumAllPaidDonationsByUser(rakanQrId, startDateTime, endDateTime);

		return new RakanQrCollection(sum);
	}

	@Override
	public Page<RakanQrDonationDetails> getAllDonationDetailsByUser(AccountUserDetails principal, Pageable pageable) {
		RakanQr agent = rakanQrService.getRakanQrByUser(principal);

		return getAllDonationDetailsByUser(agent.getId(), pageable);
	}

	@Override
	public List<RakanQrDonationDetails> getDonationDetailsListByUser(UUID rakanQrId, Pageable pageable) {
		List<RakanQrDonation> donations = rakanQrDonationRepository.findAllByRakanQr_Id(rakanQrId, pageable);

		return donations.stream().map(d -> RakanQrDonationMapper.mapToDetails(d)).toList();
	}

	@Override
	public Page<RakanQrDonationDetails> getAllDonationDetailsByUser(UUID rakanQrId, Pageable pageable) {
		Page<RakanQrDonation> donations = rakanQrDonationRepository.findByRakanQrId(rakanQrId, pageable);

		return donations.map(RakanQrDonationMapper::mapToDetails);
	}

	@Override
	public BigDecimal getTotalCollectionFromRakanQr(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = rakanQrDonationRepository.sumAllPaidDonations(startDateTime, endDateTime);

		return total;
	}

	@Transactional
	@Override
	public void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate) {
		RakanQrDonation donation = rakanQrDonationRepository.findByDonation_WebhookToken(token)
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
				rakanQrService.updateRakanQrCollectedAmountById(donation.getRakanQr().getId(),
						donation.getDonation().getAmount());
		}
	}

}
