package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.mapper.PersonalDonationMapper;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDonationServiceImpl implements PersonalDonationService {

	private final PersonalDonationRepository repository;

	@Override
	public PersonalDonationSum getDonationSumByUser(UUID id, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = repository.sumPaidDonationsByAccountId(id, startDateTime, endDateTime);

		return new PersonalDonationSum(total);
	}

	@Override
	public List<PersonalDonationDetails> getDonationDetailsListByUser(UUID personalId, Pageable pageable) {
		List<PersonalDonation> donations = repository.findAllByAccountId(personalId, pageable);

		return donations.stream().map(PersonalDonationMapper::mapToDetails).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable) {
		Page<PersonalDonation> donations = repository.findByAccountId(personalId, pageable);

		return donations.map(PersonalDonationMapper::mapToDetails);
	}

	@Override
	public PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter) {
		return getDonationSumByUser(id, filter.getStartDate(), filter.getEndDate());
	}

}
