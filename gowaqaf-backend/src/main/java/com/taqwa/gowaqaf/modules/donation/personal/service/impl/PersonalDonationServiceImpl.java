package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
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
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.payment.service.PaymentService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDonationServiceImpl implements PersonalDonationService {

	private final PersonalDonationRepository personalDonationRepository;
	private final PaymentService paymentService;
	private final PersonalService userService;

	@Override
	public PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto) {
		Personal personal = userService.getPersonalByUsername(principal.getUsername());

		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setName(null);
		paymentRequest.setAmount(dto.getAmount());

		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);

		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setName(null);
		donation.setBillingCode(response.getBillingCode());
		donation.setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		PersonalDonation saved = personalDonationRepository.save(donation);

		response.setId(saved.getId());

		return response;
	}

	@Override
	public PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId) {
		PersonalDonation donation = personalDonationRepository.findByIdAndPersonalId(donationId, personalId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		return PersonalDonationMapper.mapToDetails(donation);
	}

	@Override
	public PersonalDonationSum getDonationSumByPersonalId(UUID id, PersonalDonationSumFilter filter) {
		LocalDateTime startDateTime = filter.getStartDate() != null ? filter.getStartDate().atStartOfDay() : null;
		LocalDateTime endDateTime = filter.getEndDate() != null ? filter.getEndDate().plusDays(1).atStartOfDay() : null;

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
