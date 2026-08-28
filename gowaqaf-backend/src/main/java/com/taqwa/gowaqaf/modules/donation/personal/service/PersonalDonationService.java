package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.time.LocalDate;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface PersonalDonationService {

	PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto);

	PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId);

	PersonalDonationSum getDonationSumByPersonalId(UUID id, PersonalDonationSumFilter filter);

	PersonalCollectionSum getCollectionSum(LocalDate startDate, LocalDate endDate);

}
