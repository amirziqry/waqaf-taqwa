package com.taqwa.gowaqaf.modules.donation.merchant.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.merchant.dto.MerchantDonationSum;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.merchant.service.MerchantDonationService;
import com.taqwa.gowaqaf.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantDonationServiceImpl implements MerchantDonationService {

	private final MerchantDonationRepository merchantDonationRepository;

	@SuppressWarnings("unused")
	private final PaymentService paymentService;

	@Override
	public MerchantDonationSum getDonationSumById(UUID id) {
		BigDecimal total = merchantDonationRepository.sumPaidDonationsById(id);

		return new MerchantDonationSum(total);
	}

	@Override
	public BigDecimal getCollectionSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = merchantDonationRepository.sumAllPaidDonations(startDateTime, endDateTime);

		return total;
	}

}
