package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;

public interface PersonalDonationService {

	PaymentUrlResponse createDonation(PersonalDonationRequest dto);

	PersonalDonationSum getDonationSumById(UUID id);

}
