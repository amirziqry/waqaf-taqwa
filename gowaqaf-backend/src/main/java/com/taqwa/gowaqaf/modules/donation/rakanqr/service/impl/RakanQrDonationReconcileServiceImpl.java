package com.taqwa.gowaqaf.modules.donation.rakanqr.service.impl;

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
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.mapper.RakanQrDonationMapper;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationReconcileServiceImpl implements RakanQrDonationReconcileService {

	private final NexGenClient nexGenClient;
	private final RakanQrDonationRepository repository;
	private final RakanQrDonationService service;
	private final RakanQrService rakanQrService;

	@Value("${nexgen.collection.rakanqr}")
	private String collectionCode;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reconcile(String billingCode) {
		// Get the authoritative billing data from NexGen.
		NexGenBillingObject billing = nexGenClient.getBilling(collectionCode, billingCode);

		// Try find the donation with respective billing code locally.
		RakanQrDonation donation = repository.findByDonation_BillingCode(billingCode).orElse(null);

		// If donation exists -> synchronize information with NexGen's data.
		if (donation != null)
			updateDonationFromBilling(donation, billing);

		// If donation missing -> reconstruct information with NexGen's data.
		if (donation == null)
			reconstructDonationFromBilling(billing);

	}

	private void updateDonationFromBilling(RakanQrDonation donation, NexGenBillingObject billing) {
		PaymentStatus previousStatus = donation.getDonation().getStatus();

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

			if (previousStatus != PaymentStatus.PAID)
				rakanQrService.updateRakanQrCollectedAmountById(donation.getRakanQr().getId(),
						donation.getDonation().getAmount());
		}

	}

	private void reconstructDonationFromBilling(NexGenBillingObject billing) {
		// Get user id.
		UUID rakanQrId = getRakanQrIdFromBilling(billing);

		// Create & save donation into DB.
		Donation donation = createDonationFromBilling(billing);
		RakanQrDonation rakanQrDonation = createPersonalDonation(donation);

		service.reconstructDonation(rakanQrId, rakanQrDonation);
	}

	private UUID getRakanQrIdFromBilling(NexGenBillingObject billing) {
		if (!"rakanQrId".equals(billing.getExternalReferenceLabel1()))
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

	private RakanQrDonation createPersonalDonation(Donation donation) {
		RakanQrDonation rakanQrDonation = new RakanQrDonation();

		rakanQrDonation.setDonation(donation);
		rakanQrDonation.setRakanQr(null);

		return rakanQrDonation;
	}

	@Override
	public RakanQrDonationDetails getDonationDetailsById(UUID donationId) {
		// Find the local donation
		RakanQrDonation donation = repository.findById(donationId)
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
		return RakanQrDonationMapper.mapToDetails(donation);
	}

	@Override
	public RakanQrDonationDetails getDonationDetailsByCode(String billingCode) {
		// Find the local donation
		RakanQrDonation donation = repository.findByDonation_BillingCode(billingCode).orElse(null);

		if (donation != null) {
			PaymentStatus status = donation.getDonation().getStatus();

			// Already paid -> no need to call NexGen
			if (status == PaymentStatus.PAID || status == PaymentStatus.EXPIRED)
				return RakanQrDonationMapper.mapToDetails(donation);

			// Still pending/unpaid -> force reconciliation with NexGen
			if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
				reconcile(donation.getDonation().getBillingCode());

				// Re-fetch because reconcile may have updated the entity
				donation = repository.findById(donation.getId())
						.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

				if (donation.getDonation().getStatus() != PaymentStatus.PAID)
					rakanQrService.updateRakanQrCollectedAmountById(donation.getRakanQr().getId(),
							donation.getDonation().getAmount());
			}
		}

		if (donation == null) {
			reconcile(billingCode);

			donation = repository.findByDonation_BillingCode(billingCode)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

			if (donation.getDonation().getStatus() != PaymentStatus.PAID)
				rakanQrService.updateRakanQrCollectedAmountById(donation.getRakanQr().getId(),
						donation.getDonation().getAmount());
		}

		// Return the updated donation.
		return RakanQrDonationMapper.mapToDetails(donation);
	}

}
