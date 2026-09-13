package com.taqwa.gowaqaf.modules.donation.project.service;

import java.util.UUID;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface ProjectDonationPaymentService {

	PaymentUrlResponse createDonationByProjectId(AccountUserDetails principal, UUID projectId,
			ProjectDonationRequest dto);

}
