package com.taqwa.gowaqaf.modules.donation.agent.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationSum;

public interface RakanQrDonationService {

	RakanQrDonationSum getDonationSumByUser(Authentication authentication, RakanQrDonationFilter filter);

	BigDecimal getCollectionSum(LocalDate startDate, LocalDate endDate);

}
