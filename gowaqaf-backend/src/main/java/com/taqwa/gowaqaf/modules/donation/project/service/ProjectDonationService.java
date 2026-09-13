package com.taqwa.gowaqaf.modules.donation.project.service;

import java.time.LocalDate;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface ProjectDonationService {

	ProjectDonationDetails getPaymentStatus(AccountUserDetails principal, UUID donationId);

	ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, ProjectCollectionSumFilter filter);

	ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, LocalDate startDate, LocalDate endDate);

	void reconstructDonation(UUID personalId, UUID projectId, ProjectDonation projectDonation);

}
