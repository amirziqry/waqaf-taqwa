package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.mapper.PersonalDonationMapper;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDonationServiceImpl implements PersonalDonationService {

	private final DonationRepository donationRepository;
	private final PersonalDonationRepository repository;
	private final PersonalService personalService;

	@Override
	public void reconstructDonation(UUID personalId, PersonalDonation personalDonation) {
		Personal user = personalService.getPersonalById(personalId);

		Donation donation = donationRepository.save(personalDonation.getDonation());

		personalDonation.setId(donation.getId());
		personalDonation.setDonation(donation);
		personalDonation.setPersonal(user);

		repository.save(personalDonation);
	}

	@Override
	public PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId) {
		PersonalDonation donation = repository.findByIdAndPersonalId(donationId, personalId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		return PersonalDonationMapper.mapToDetails(donation);
	}

	@Override
	public List<PersonalDonationDetails> getDonationDetailsListByUser(UUID personalId, Pageable pageable) {
		List<PersonalDonation> donations = repository.findAllByPersonalId(personalId, pageable);

		return donations.stream().map(PersonalDonationMapper::mapToDetails).toList();
	}

	@Override
	public Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable) {
		Page<PersonalDonation> donations = repository.findByPersonalId(personalId, pageable);

		return donations.map(PersonalDonationMapper::mapToDetails);
	}

	@Override
	public PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter) {
		return getDonationSumByUser(id, filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public PersonalDonationSum getDonationSumByUser(UUID id, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = repository.sumPaidDonationsByPersonalId(id, startDateTime, endDateTime);

		return new PersonalDonationSum(total);
	}

	@Override
	public PersonalCollectionSum getCollectionSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		PersonalCollectionSum total = repository.sumPaidDonationsByType(startDateTime, endDateTime);

		return total;
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

}
