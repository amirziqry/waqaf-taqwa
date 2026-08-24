package com.taqwa.gowaqaf.modules.donation.donator.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationRequest;
import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationSum;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;

public interface DonatorDonationService {

	PaymentUrlResponse createDonation(DonatorDonationRequest dto);

	DonatorDonationSum getDonationSumById(UUID id);

}
