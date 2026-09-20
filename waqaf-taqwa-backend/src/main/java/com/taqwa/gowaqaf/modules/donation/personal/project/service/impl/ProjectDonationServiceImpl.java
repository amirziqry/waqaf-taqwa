package com.taqwa.gowaqaf.modules.donation.personal.project.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.mapper.PersonalDonationMapper;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationServiceImpl implements ProjectDonationService {

	private final PersonalDonationRepository repository;
	private final ProjectService projectService;
	private final ProjectDonationReconcileService reconcileService;

	@Value("${nexgen.collection.project}")
	private String collectionCode;

	@Override
	@Transactional
	public PersonalDonationDetails getDonationDetailsById(UUID personalId, UUID donationId) {
		// Find the local donation
		PersonalDonation donation = repository.findByIdAndAccountId(donationId, personalId).orElse(null);

		// Get current status
		PaymentStatus status = donation.getTransaction().getStatus();

		// For pending/unpaid -> force reconciliation with NexGen
		if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
			reconcileService.reconcile(donation.getTransaction().getBillingCode());

			// Re-fetch because reconcile may have updated the entity
			donation = repository.findById(donationId)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));
		}

		// Return the latest status
		return PersonalDonationMapper.mapToDetails(donation);
	}

	@Override
	@Transactional
	public PersonalDonationDetails getDonationDetailsByCode(UUID personalId, String billingCode) {
		// Find the local donation
		PersonalDonation donation = repository.findByAccount_IdAndTransaction_BillingCode(personalId, billingCode)
				.orElse(null);

		if (donation != null) {
			PaymentStatus status = donation.getTransaction().getStatus();

			// Already paid -> no need to call NexGen
			if (status == PaymentStatus.PAID || status == PaymentStatus.EXPIRED)
				return PersonalDonationMapper.mapToDetails(donation);

			// Still pending/unpaid -> force reconciliation with NexGen
			if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
				reconcileService.reconcile(donation.getTransaction().getBillingCode());

				// Re-fetch because reconcile may have updated the entity
				donation = repository.findById(donation.getId())
						.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

				if (donation.getTransaction().getStatus() != PaymentStatus.PAID)
					projectService.updateProjectCollectedAmountById(donation.getProject().getId(),
							donation.getTransaction().getAmount());
			}
		}

		if (donation == null) {
			reconcileService.reconcile(billingCode);

			donation = repository.findByTransaction_BillingCode(billingCode)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

			if (donation.getTransaction().getStatus() != PaymentStatus.PAID)
				projectService.updateProjectCollectedAmountById(donation.getProject().getId(),
						donation.getTransaction().getAmount());
		}

		// Return the updated donation.
		return PersonalDonationMapper.mapToDetails(donation);
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

}
