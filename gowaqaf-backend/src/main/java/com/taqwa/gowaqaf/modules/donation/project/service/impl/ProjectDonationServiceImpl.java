package com.taqwa.gowaqaf.modules.donation.project.service.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.personal.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationRequest;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationSum;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.organization.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.project.service.ProjectService;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationServiceImpl implements ProjectDonationService {

	private final PersonalDonationRepository personalDonationRepository;
	private final ProjectService projectService;
	private final PaymentService paymentService;

	@Override
	public PaymentUrlResponse createDonationByProjectId(UUID projectId, ProjectDonationRequest dto) {
		Project project = projectService.getProjectById(projectId);

		// Get name from principal.
		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setName(null);
		paymentRequest.setAmount(dto.getAmount());

		// TODO: Pass collection code
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);

		PersonalDonation donation = new PersonalDonation();
		donation.setName(null);
		donation.setBillingCode(response.getBillingCode());
		donation.setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));
		donation.setDonationType(DonationType.PROJECT);
		donation.setProject(project);

		PersonalDonation saved = personalDonationRepository.save(donation);

		response.setId(saved.getId());

		return response;
	}

	@Override
	public ProjectDonationSum getProjectDonationSumById(UUID id) {
		BigDecimal total = personalDonationRepository.sumPaidDonationsByProjectId(id);

		return new ProjectDonationSum(total);
	}

}
