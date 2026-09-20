package com.taqwa.gowaqaf.modules.donation.personal.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.personal.project.repository.ProjectDonationAdminRepository;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationAdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationAdminServiceImpl implements ProjectDonationAdminService {

	private final ProjectDonationAdminRepository repository;

	@Override
	public ProjectCollectionSum getCollectedAmountByProjectId(UUID projectId, ProjectCollectionSumFilter filter) {
		return getCollectedAmountByProjectId(projectId, filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public ProjectCollectionSum getCollectedAmountByProjectId(UUID projectId, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = repository.sumPaidDonationsByProjectId(projectId, startDateTime, endDateTime);

		return new ProjectCollectionSum(total);
	}

}
