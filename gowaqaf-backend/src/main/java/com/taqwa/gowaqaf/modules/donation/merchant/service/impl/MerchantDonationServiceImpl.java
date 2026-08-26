package com.taqwa.gowaqaf.modules.donation.merchant.service.impl;

import java.math.BigDecimal;
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

}
