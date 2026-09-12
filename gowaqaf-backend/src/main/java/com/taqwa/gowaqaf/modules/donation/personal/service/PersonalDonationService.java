package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public interface PersonalDonationService {

	PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId);

	PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter);

	PersonalDonationSum getDonationSumByUser(UUID id, LocalDate startDate, LocalDate endDate);

	PersonalCollectionSum getCollectionSum(LocalDate startDate, LocalDate endDate);

	Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable);

	List<PersonalDonationDetails> getDonationDetailsListByUser(UUID personalId, Pageable pageable);

	void reconstructDonation(UUID personalId, PersonalDonation personalDonation);

}
