package com.taqwa.gowaqaf.modules.donation.merchant.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.merchant.dto.MerchantDonationSum;

public interface MerchantDonationService {

	MerchantDonationSum getDonationSumById(UUID id);

	BigDecimal getCollectionSum(LocalDate startDate, LocalDate endDate);

}
