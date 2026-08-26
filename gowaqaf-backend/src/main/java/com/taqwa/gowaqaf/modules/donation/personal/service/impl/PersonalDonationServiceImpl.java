package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDonationServiceImpl implements PersonalDonationService {

	private final PersonalDonationRepository personalDonationRepository;
	private final PaymentService paymentService;

	@Override
	public PaymentUrlResponse createDonation(PersonalDonationRequest dto) {

		// Get name from principal.

		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setName(null);
		paymentRequest.setAmount(dto.getAmount());

		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);

		PersonalDonation donation = new PersonalDonation();
		donation.setName(null);
		donation.setBillingCode(response.getBillingCode());
		donation.setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		PersonalDonation saved = personalDonationRepository.save(donation);

		response.setId(saved.getId());

		return response;
	}

	@Override
	public PersonalDonationSum getDonationSumById(UUID id) {
		BigDecimal total = personalDonationRepository.sumPaidDonationsById(id);

		return new PersonalDonationSum(total);
	}

}
