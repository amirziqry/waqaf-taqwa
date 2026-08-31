package com.taqwa.gowaqaf.modules.donation.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.mapper.ProjectDonationMapper;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.payment.service.PaymentService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationServiceImpl implements ProjectDonationService {

	private final PersonalDonationRepository personalDonationRepository;
	private final PersonalService userService;
	private final ProjectService projectService;
	private final PaymentService paymentService;

	@Override
	public PaymentUrlResponse createDonationByProjectId(AccountUserDetails principal, UUID projectId,
			PersonalDonationRequest dto) {
		Personal personal = userService.getPersonalByUsername(principal.getUsername());
		Project project = projectService.getProjectById(projectId);

		// Get name from principal.
		PaymentRequest paymentRequest = new PaymentRequest();
		paymentRequest.setName(null);
		paymentRequest.setAmount(dto.getAmount());

		// TODO: Pass collection code
		PaymentUrlResponse response = paymentService.createPaymentBill(paymentRequest);

		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setName(null); // TODO: User or payer name
		donation.setBillingCode(response.getBillingCode());
		donation.setStatus(PaymentStatus.valueOf(response.getStatus().toUpperCase()));
		donation.setDonationType(DonationType.PROJECT);
		donation.setProject(project);

		PersonalDonation saved = personalDonationRepository.save(donation);

		response.setId(saved.getId());

		return response;
	}

	@Override
	public ProjectDonationDetails getPaymentStatus(UUID personalId, UUID donationId) {
		PersonalDonation donation = personalDonationRepository.findByIdAndPersonalId(donationId, personalId)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		return ProjectDonationMapper.mapToDetails(donation);
	}

	@Override
	public ProjectCollectionSum getProjectCollectionSumById(UUID projectId, ProjectCollectionSumFilter filter) {
		return getProjectCollectionSumById(projectId, filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public ProjectCollectionSum getProjectCollectionSumById(UUID projectId, LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = personalDonationRepository.sumPaidDonationsByProjectId(projectId, startDateTime,
				endDateTime);

		return new ProjectCollectionSum(total);
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

}
