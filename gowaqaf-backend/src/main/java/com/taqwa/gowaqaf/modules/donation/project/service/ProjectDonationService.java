package com.taqwa.gowaqaf.modules.donation.project.service;

import java.time.LocalDate;
import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface ProjectDonationService {

	PaymentUrlResponse createDonationByProjectId(AccountUserDetails principal, UUID projectId,
			PersonalDonationRequest dto);

	ProjectDonationDetails getPaymentStatus(UUID personalId, UUID donationId);

	ProjectCollectionSum getProjectCollectionSumById(UUID projectId, ProjectCollectionSumFilter filter);

	ProjectCollectionSum getProjectCollectionSumById(UUID projectId, LocalDate startDate, LocalDate endDate);

}
