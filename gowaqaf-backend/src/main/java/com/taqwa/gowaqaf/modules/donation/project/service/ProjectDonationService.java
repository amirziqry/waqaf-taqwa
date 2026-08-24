package com.taqwa.gowaqaf.modules.donation.project.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationSum;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;

public interface ProjectDonationService {

	PaymentUrlResponse createDonationByProjectId(UUID projectId, ProjectDonationRequest dto);

	ProjectDonationSum getProjectDonationSumById(UUID id);

}
