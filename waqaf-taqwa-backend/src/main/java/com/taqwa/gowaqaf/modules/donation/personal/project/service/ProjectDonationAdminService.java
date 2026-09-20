package com.taqwa.gowaqaf.modules.donation.personal.project.service;

import java.time.LocalDate;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectCollectionSumFilter;

public interface ProjectDonationAdminService {

	ProjectCollectionSum getCollectedAmountByProjectId(UUID projectId, ProjectCollectionSumFilter filter);

	ProjectCollectionSum getCollectedAmountByProjectId(UUID projectId, LocalDate startDate, LocalDate endDate);

}
