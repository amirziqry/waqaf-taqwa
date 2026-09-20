package com.taqwa.gowaqaf.modules.donation.personal.project.service;

import java.util.UUID;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface ProjectDonationPaymentService {

	PaymentUrlResponse createDonationByProjectId(CustomUserDetails principal, UUID projectId,
			ProjectDonationRequest dto);

}
