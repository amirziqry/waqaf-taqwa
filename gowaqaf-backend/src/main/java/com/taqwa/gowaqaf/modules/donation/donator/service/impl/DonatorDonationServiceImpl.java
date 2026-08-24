package com.taqwa.gowaqaf.modules.donation.donator.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationRequest;
import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationSum;
import com.taqwa.gowaqaf.modules.donation.donator.entity.DonatorDonation;
import com.taqwa.gowaqaf.modules.donation.donator.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.donator.repository.DonatorDonationRepository;
import com.taqwa.gowaqaf.modules.donation.donator.service.DonatorDonationService;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonatorDonationServiceImpl implements DonatorDonationService {

	private final DonatorDonationRepository donatorDonationRepository;
	private final PaymentService paymentService;

	@Override
	public PaymentUrlResponse createDonation(DonatorDonationRequest dto) {

		// Get name from principal.

		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setName(null);
		paymentRequest.setAmount(dto.getAmount());

		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);

		DonatorDonation donation = new DonatorDonation();
		donation.setName(null);
		donation.setBillingCode(response.getBillingCode());
		donation.setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));

		DonatorDonation saved = donatorDonationRepository.save(donation);

		response.setId(saved.getId());

		return response;
	}

	@Override
	public DonatorDonationSum getDonationSumById(UUID id) {
		BigDecimal total = donatorDonationRepository.sumPaidDonationsById(id);

		return new DonatorDonationSum(total);
	}

}
