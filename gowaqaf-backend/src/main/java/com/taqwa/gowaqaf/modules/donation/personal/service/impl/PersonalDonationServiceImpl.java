package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

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
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.external.payment.service.PaymentService;
import com.taqwa.gowaqaf.external.payment.webhook.service.WebhookService;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.mapper.PersonalDonationMapper;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDonationServiceImpl implements PersonalDonationService {

	private final DonationRepository donationRepository;
	private final PersonalDonationRepository personalDonationRepository;
	private final PersonalService userService;
	private final WebhookService webhookService;
	private final PaymentService paymentService;

	@Value("${nexgen.collection.personal}")
	private String collectionCode;

	@Transactional
	@Override
	public PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto) {
		// Get account.
		Personal personal = userService.getPersonalByUsername(principal.getUsername());

		// Generate webhook token.
		String webhookToken = webhookService.generateWebhookToken();
		String callbackUrl = webhookService.buildWebhookUrl("personal", webhookToken);

		// Build donation object.
		PersonalDonation donation = buildDonationDetails(personal, dto, webhookToken);

		// Build payment request dto.
		PaymentRequest paymentRequest = buildPaymentRequest(collectionCode, personal, donation, dto.getRedirectUrl(),
				callbackUrl);

		// Call payment service (Gateway router).
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);
		response.setId(donation.getId());

		// Update donation object.
		donation.getDonation().setBillingCode(response.getBillingCode());
		donation.getDonation().setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		return response;
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

		return personalDonationRepository.saveAndFlush(personalDonation);
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

		return paymentRequest;
	}

	@Override
	public PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId) {
		PersonalDonation donation = personalDonationRepository.findByIdAndPersonalId(donationId, personalId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		return PersonalDonationMapper.mapToDetails(donation);
	}

	@Override
	public List<PersonalDonationDetails> getDonationDetailsListByUser(UUID personalId, Pageable pageable) {
		List<PersonalDonation> donations = personalDonationRepository.findAllByPersonalId(personalId, pageable);

		return donations.stream().map(PersonalDonationMapper::mapToDetails).toList();
	}

	@Override
	public Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable) {
		Page<PersonalDonation> donations = personalDonationRepository.findByPersonalId(personalId, pageable);

		return donations.map(PersonalDonationMapper::mapToDetails);
	}

	@Override
	public PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter) {
		return getDonationSumByUser(id, filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public PersonalDonationSum getDonationSumByUser(UUID id, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = personalDonationRepository.sumPaidDonationsByPersonalId(id, startDateTime, endDateTime);

		return new PersonalDonationSum(total);
	}

	@Override
	public PersonalCollectionSum getCollectionSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		PersonalCollectionSum total = personalDonationRepository.sumPaidDonationsByType(startDateTime, endDateTime);

		return total;
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

}
