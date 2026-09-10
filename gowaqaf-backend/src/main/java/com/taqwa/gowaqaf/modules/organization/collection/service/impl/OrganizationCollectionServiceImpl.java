package com.taqwa.gowaqaf.modules.organization.collection.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSumFilter;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.organization.collection.service.OrganizationCollectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationCollectionServiceImpl implements OrganizationCollectionService {

	private final DonationRepository donationRepository;

	@Override
	public OrgCollectionInfo getDonationCollectionSum(OrganizationCollectionSumFilter filter) {

		return getDonationCollectionSum(filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public OrgCollectionInfo getDonationCollectionSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		OrgCollectionInfo dto = donationRepository.getDonationCollectionSum(startDateTime, endDateTime);

		return dto;
	}

}
