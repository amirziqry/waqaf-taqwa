package com.taqwa.gowaqaf.modules.donation.personal.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.payment.client.nexgen.client.NexGenClient;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenBillingObject;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.mapper.PersonalDonationMapper;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Fallback for case Webhook token fails to update a donation.
 * <p>
 */

@Service
@RequiredArgsConstructor
public class PersonalDonationReconcileServiceImpl implements PersonalDonationReconcileService {

	private final NexGenClient nexGenClient;
	private final PersonalDonationRepository repository;
	private final PersonalDonationService service;

	@Value("${nexgen.collection.personal}")
	private String collectionCode;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reconcile(String billingCode) {
		// Get the authoritative billing data from NexGen.
		NexGenBillingObject billing = nexGenClient.getBilling(collectionCode, billingCode);

		// Try find the donation with respective billing code locally.
		PersonalDonation donation = repository.findByDonation_BillingCode(billingCode).orElse(null);

		// If donation exists -> synchronize information with NexGen's data.
		if (donation != null)
			updateDonationFromBilling(donation, billing);

		// If donation missing -> reconstruct information with NexGen's data.
		if (donation == null)
			reconstructDonationFromBilling(billing);

	}

	private void updateDonationFromBilling(PersonalDonation donation, NexGenBillingObject billing) {
		// Check and update payment status.
		switch (billing.getStatus().toLowerCase()) {
		case "paid" -> donation.getDonation().setStatus(PaymentStatus.PAID);
		case "pending" -> donation.getDonation().setStatus(PaymentStatus.PENDING);
		case "unpaid" -> donation.getDonation().setStatus(PaymentStatus.UNPAID);
		default -> donation.getDonation().setStatus(PaymentStatus.EXPIRED);
		}

		if (donation.getDonation().getStatus() == PaymentStatus.PAID) {
			String transactionId = billing.getPaymentMethodDetail().getTransactionId();
			String orderId = billing.getPaymentMethodDetail().getOrderId();
			LocalDateTime transactionDate = billing.getPaymentMethodDetail().getTransactionDate();

			// Update amount to real paid amount.
			donation.getDonation().setAmount(billing.getAmount());

			// Set transaction/order id.
			donation.getDonation().setTransactionId(transactionId != null ? transactionId : orderId);

			// Set transaction/paid time.
			donation.getDonation().setPaidAt(transactionDate);

			// Nullify webhook token.
			donation.getDonation().setWebhookToken(null);
		}

	}

	private void reconstructDonationFromBilling(NexGenBillingObject billing) {
		// Get user id.
		UUID personalId = getPersonalIdFromBilling(billing);

		// Create & save donation into DB.
		Donation donation = createDonationFromBilling(billing);
		PersonalDonation personalDonation = createPersonalDonation(donation);

		service.reconstructDonation(personalId, personalDonation);
	}

	private UUID getPersonalIdFromBilling(NexGenBillingObject billing) {
		if (!"personalId".equals(billing.getExternalReferenceLabel1()))
			throw new BadRequestException(ErrorCode.WHK017, "Failed to process request.");

		try {
			return UUID.fromString(billing.getExternalReferenceValue1());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(ErrorCode.WHK018, "Failed to process request.");
		}
	}

	private Donation createDonationFromBilling(NexGenBillingObject billing) {
		Donation donation = new Donation();

		String transactionId = billing.getPaymentMethodDetail().getTransactionId();
		String orderId = billing.getPaymentMethodDetail().getOrderId();

		donation.setBillingCode(billing.getCode());
		donation.setTransactionId(transactionId != null ? transactionId : orderId);
		donation.setAmount(billing.getAmount());
		donation.setPaidAt(billing.getPaymentMethodDetail().getTransactionDate());
		donation.setDonationType(DonationType.DIRECT);

		switch (billing.getStatus().toLowerCase()) {
		case "paid" -> donation.setStatus(PaymentStatus.PAID);
		case "pending" -> donation.setStatus(PaymentStatus.PENDING);
		case "unpaid" -> donation.setStatus(PaymentStatus.UNPAID);
		default -> donation.setStatus(PaymentStatus.EXPIRED);
		}

		return donation;
	}

	private PersonalDonation createPersonalDonation(Donation donation) {
		PersonalDonation personalDonation = new PersonalDonation();

		personalDonation.setDonation(donation);
		personalDonation.setPersonal(null);
		personalDonation.setReceiptHashId(null);
		personalDonation.setTaxExempt(false);

		return personalDonation;
	}

	@Override
	public PersonalDonationDetails getDonationDetailsById(UUID personalID, UUID donationId) {
		// Find the local donation
		PersonalDonation donation = repository.findByIdAndPersonalId(donationId, personalID)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

		PaymentStatus status = donation.getDonation().getStatus();

		// For pending/unpaid -> force reconciliation with NexGen
		if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
			reconcile(donation.getDonation().getBillingCode());

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
		PersonalDonation donation = repository.findByPersonal_IdAndDonation_BillingCode(personalId, billingCode)
				.orElse(null);

		if (donation != null) {
			PaymentStatus status = donation.getDonation().getStatus();

			// Already paid -> no need to call NexGen
			if (status == PaymentStatus.PAID || status == PaymentStatus.EXPIRED)
				return PersonalDonationMapper.mapToDetails(donation);

			// Still pending/unpaid -> force reconciliation with NexGen
			if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
				reconcile(donation.getDonation().getBillingCode());

				// Re-fetch because reconcile may have updated the entity
				donation = repository.findById(donation.getId())
						.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));
			}
		}

		if (donation == null) {
			reconcile(billingCode);

			donation = repository.findByPersonal_IdAndDonation_BillingCode(personalId, billingCode)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));
		}

		// Return the updated donation.
		return PersonalDonationMapper.mapToDetails(donation);
	}

}
