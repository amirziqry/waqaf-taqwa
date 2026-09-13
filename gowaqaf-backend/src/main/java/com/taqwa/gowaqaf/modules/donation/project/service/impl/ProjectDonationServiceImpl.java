package com.taqwa.gowaqaf.modules.donation.project.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSumFilter;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;
import com.taqwa.gowaqaf.modules.donation.project.mapper.ProjectDonationMapper;
import com.taqwa.gowaqaf.modules.donation.project.repository.ProjectDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationServiceImpl implements ProjectDonationService {

	private final ProjectDonationRepository repository;
	private final TransactionRepository transactionRepository;
	private final ProjectService projectService;
	private final PersonalService personalService;

	@Value("${nexgen.collection.project}")
	private String collectionCode;

	@Override
	public void reconstructDonation(UUID personalId, UUID projectId, ProjectDonation projectDonation) {
		Personal personal = personalService.getPersonalById(personalId);

		Project project = projectService.getProjectById(projectId);

		Transaction transaction = transactionRepository.save(projectDonation.getTransaction());

		projectDonation.setId(transaction.getId());
		projectDonation.setTransaction(transaction);
		projectDonation.setProject(project);
		projectDonation.setPersonal(personal);
		projectDonation.setTaxExempt(false);

		ProjectDonation saved = repository.save(projectDonation);

		if (saved.getTransaction().getStatus() != PaymentStatus.PAID)
			projectService.updateProjectCollectedAmountById(saved.getProject().getId(),
					saved.getTransaction().getAmount());
	}

	@Override
	public ProjectDonationDetails getPaymentStatus(AccountUserDetails principal, UUID donationId) {
		ProjectDonation donation = repository.findByIdAndPersonalId(donationId, principal.getId())
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Donation ID not found"));

		return ProjectDonationMapper.mapToDetails(donation);
	}

	@Override
	public ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, ProjectCollectionSumFilter filter) {
		return getDonationCollectionByProjectId(projectId, filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public ProjectCollectionSum getDonationCollectionByProjectId(UUID projectId, LocalDate startDate,
			LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = repository.sumPaidDonationsByProjectId(projectId, startDateTime, endDateTime);

		return new ProjectCollectionSum(total);
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

}
