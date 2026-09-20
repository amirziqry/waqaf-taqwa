package com.taqwa.gowaqaf.modules.donation.personal.direct.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationService;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.mapper.PersonalDonationMapper;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DirectDonationServiceImpl implements DirectDonationService {

	private final PersonalDonationRepository repository;
	private final DirectDonationReconcileService reconcileService;

	@Override
	public PersonalDonationDetails getDonationDetailsById(UUID personalID, UUID donationId) {
		// Find the local donation
		PersonalDonation donation = repository.findByIdAndAccountId(donationId, personalID)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

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
			}
		}

		if (donation == null) {
			reconcileService.reconcile(billingCode);

			donation = repository.findByAccount_IdAndTransaction_BillingCode(personalId, billingCode)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));
		}

		// Return the updated donation.
		return PersonalDonationMapper.mapToDetails(donation);
	}

	@SuppressWarnings("unused")
	private void generateReceiptHashId() {
		// TODO
	}

}
