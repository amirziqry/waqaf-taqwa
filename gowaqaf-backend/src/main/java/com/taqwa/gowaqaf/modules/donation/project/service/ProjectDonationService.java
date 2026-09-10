package com.taqwa.gowaqaf.modules.donation.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface ProjectDonationService {

	PaymentUrlResponse createDonationByProjectId(AccountUserDetails principal, UUID projectId,
			ProjectDonationRequest dto);

	ProjectDonationDetails getPaymentStatus(AccountUserDetails principal, UUID donationId);

	ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, ProjectCollectionSumFilter filter);

	ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, LocalDate startDate, LocalDate endDate);

	void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate);

}
