package com.taqwa.gowaqaf.modules.donation.vendor.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.vendor.dto.VendorDonationSum;
import com.taqwa.gowaqaf.modules.donation.vendor.repository.VendorDonationRepository;
import com.taqwa.gowaqaf.modules.donation.vendor.service.VendorDonationService;
import com.taqwa.gowaqaf.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorDonationServiceImpl implements VendorDonationService {

	private final VendorDonationRepository vendorDonationRepository;

	@SuppressWarnings("unused")
	private final PaymentService paymentService;

	@Override
	public VendorDonationSum getDonationSumById(UUID id) {
		BigDecimal total = vendorDonationRepository.sumPaidDonationsById(id);

		return new VendorDonationSum(total);
	}

}
