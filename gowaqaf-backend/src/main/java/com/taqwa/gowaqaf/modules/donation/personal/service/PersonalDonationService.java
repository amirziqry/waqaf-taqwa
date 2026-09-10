package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface PersonalDonationService {

	PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto);

	PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId);

	PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter);

	PersonalDonationSum getDonationSumByUser(UUID id, LocalDate startDate, LocalDate endDate);

	PersonalCollectionSum getCollectionSum(LocalDate startDate, LocalDate endDate);

	Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable);

	List<PersonalDonationDetails> getDonationDetailsListByUser(UUID personalId, Pageable pageable);

}
