package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;

public interface PersonalDonationService {

	PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter);

	PersonalDonationSum getDonationSumByUser(UUID id, LocalDate startDate, LocalDate endDate);

	Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable);

	List<PersonalDonationDetails> getDonationDetailsListByUser(UUID personalId, Pageable pageable);

}
