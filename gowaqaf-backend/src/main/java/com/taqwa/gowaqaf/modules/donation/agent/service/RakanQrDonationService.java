package com.taqwa.gowaqaf.modules.donation.agent.service;

import org.springframework.security.core.Authentication;

import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationSum;

public interface RakanQrDonationService {

	RakanQrDonationSum getDonationSum(Authentication authentication, RakanQrDonationFilter filter);

}
