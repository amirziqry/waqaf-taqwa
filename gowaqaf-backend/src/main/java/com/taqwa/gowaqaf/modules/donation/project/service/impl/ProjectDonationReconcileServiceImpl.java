package com.taqwa.gowaqaf.modules.donation.project.service.impl;

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
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;
import com.taqwa.gowaqaf.modules.donation.project.mapper.ProjectDonationMapper;
import com.taqwa.gowaqaf.modules.donation.project.repository.ProjectDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.project.service.ProjectDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationReconcileServiceImpl implements ProjectDonationReconcileService {

	private final NexGenClient nexGenClient;
	private final ProjectDonationRepository repository;
	private final ProjectDonationService service;
	private final ProjectService projectService;

	@Value("${nexgen.collection.project}")
	private String collectionCode;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reconcile(String billingCode) {
		// Get the authoritative billing data from NexGen.
		NexGenBillingObject billing = nexGenClient.getBilling(collectionCode, billingCode);

		// Try find the donation with respective billing code locally.
		ProjectDonation donation = repository.findByTransaction_BillingCode(billingCode).orElse(null);

		// If donation exists -> synchronize information with NexGen's data.
		if (donation != null)
			updateDonationFromBilling(donation, billing);

		// If donation missing -> reconstruct information with NexGen's data.
		if (donation == null)
			reconstructDonationFromBilling(billing);

	}

	private void updateDonationFromBilling(ProjectDonation donation, NexGenBillingObject billing) {
		PaymentStatus previousStatus = donation.getTransaction().getStatus();

		// Check and update payment status.
		switch (billing.getStatus().toLowerCase()) {
		case "paid" -> donation.getTransaction().setStatus(PaymentStatus.PAID);
		case "pending" -> donation.getTransaction().setStatus(PaymentStatus.PENDING);
		case "unpaid" -> donation.getTransaction().setStatus(PaymentStatus.UNPAID);
		default -> donation.getTransaction().setStatus(PaymentStatus.EXPIRED);
		}

		if (donation.getTransaction().getStatus() == PaymentStatus.PAID) {
			String transactionId = billing.getPaymentMethodDetail().getTransactionId();
			String orderId = billing.getPaymentMethodDetail().getOrderId();
			LocalDateTime transactionDate = billing.getPaymentMethodDetail().getTransactionDate();

			// Update amount to real paid amount.
			donation.getTransaction().setAmount(billing.getAmount());

			// Set transaction/order id.
			donation.getTransaction().setTransactionId(transactionId != null ? transactionId : orderId);

			// Set transaction/paid time.
			donation.getTransaction().setPaidAt(transactionDate);

			// Nullify webhook token.
			donation.getTransaction().setWebhookToken(null);

			if (previousStatus != PaymentStatus.PAID)
				projectService.updateProjectCollectedAmountById(donation.getProject().getId(),
						donation.getTransaction().getAmount());
		}

		repository.saveAndFlush(donation);
	}

	private void reconstructDonationFromBilling(NexGenBillingObject billing) {
		// Get user id.
		UUID userId = getUserIdFromBilling(billing);

		// Get project id.
		UUID projectId = getProjectIdFromBilling(billing);

		// Create & save donation into DB.
		Transaction transaction = createDonationFromBilling(billing);
		ProjectDonation projectDonation = createProjectDonation(transaction);

		service.reconstructDonation(userId, projectId, projectDonation);
	}

	private UUID getUserIdFromBilling(NexGenBillingObject billing) {
		if (!"personalId".equals(billing.getExternalReferenceLabel1()))
			throw new BadRequestException(ErrorCode.WHK017, "Failed to process request.");

		try {
			return UUID.fromString(billing.getExternalReferenceValue1());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(ErrorCode.WHK018, "Failed to process request.");
		}
	}

	private UUID getProjectIdFromBilling(NexGenBillingObject billing) {
		if (!"projectId".equals(billing.getExternalReferenceLabel2()))
			throw new BadRequestException(ErrorCode.WHK017, "Failed to process request.");

		try {
			return UUID.fromString(billing.getExternalReferenceValue2());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(ErrorCode.WHK018, "Failed to process request.");
		}
	}

	private Transaction createDonationFromBilling(NexGenBillingObject billing) {
		Transaction transaction = new Transaction();

		String transactionId = billing.getPaymentMethodDetail().getTransactionId();
		String orderId = billing.getPaymentMethodDetail().getOrderId();

		transaction.setBillingCode(billing.getCode());
		transaction.setTransactionId(transactionId != null ? transactionId : orderId);
		transaction.setAmount(billing.getAmount());
		transaction.setPaidAt(billing.getPaymentMethodDetail().getTransactionDate());
		transaction.setDonationType(DonationType.DIRECT);

		switch (billing.getStatus().toLowerCase()) {
		case "paid" -> transaction.setStatus(PaymentStatus.PAID);
		case "pending" -> transaction.setStatus(PaymentStatus.PENDING);
		case "unpaid" -> transaction.setStatus(PaymentStatus.UNPAID);
		default -> transaction.setStatus(PaymentStatus.EXPIRED);
		}

		return transaction;
	}

	private ProjectDonation createProjectDonation(Transaction transaction) {
		ProjectDonation projectDonation = new ProjectDonation();

		projectDonation.setTransaction(transaction);
		projectDonation.setProject(null);
		projectDonation.setPersonal(null);

		return projectDonation;
	}

	@Override
	@Transactional
	public ProjectDonationDetails getDonationDetailsById(AccountUserDetails principal, UUID donationId) {
		// Find the local donation
		ProjectDonation donation = repository.findByPersonal_IdAndTransaction_Id(principal.getId(), donationId)
				.orElse(null);

		PaymentStatus status = donation.getTransaction().getStatus();

		// For pending/unpaid -> force reconciliation with NexGen
		if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
			reconcile(donation.getTransaction().getBillingCode());

			// Re-fetch because reconcile may have updated the entity
			donation = repository.findById(donationId)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));
		}

		// Return the latest status
		return ProjectDonationMapper.mapToDetails(donation);
	}

	@Override
	@Transactional
	public ProjectDonationDetails getDonationDetailsByCode(AccountUserDetails principal, String billingCode) {
		// Find the local donation
		ProjectDonation donation = repository
				.findByPersonal_IdAndTransaction_BillingCode(principal.getId(), billingCode).orElse(null);

		if (donation != null) {
			PaymentStatus status = donation.getTransaction().getStatus();

			// Already paid -> no need to call NexGen
			if (status == PaymentStatus.PAID || status == PaymentStatus.EXPIRED)
				return ProjectDonationMapper.mapToDetails(donation);

			// Still pending/unpaid -> force reconciliation with NexGen
			if (status == PaymentStatus.PENDING || status == PaymentStatus.UNPAID) {
				reconcile(donation.getTransaction().getBillingCode());

				// Re-fetch because reconcile may have updated the entity
				donation = repository.findById(donation.getId())
						.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

				if (donation.getTransaction().getStatus() != PaymentStatus.PAID)
					projectService.updateProjectCollectedAmountById(donation.getProject().getId(),
							donation.getTransaction().getAmount());
			}
		}

		if (donation == null) {
			reconcile(billingCode);

			donation = repository.findByTransaction_BillingCode(billingCode)
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

			if (donation.getTransaction().getStatus() != PaymentStatus.PAID)
				projectService.updateProjectCollectedAmountById(donation.getProject().getId(),
						donation.getTransaction().getAmount());
		}

		// Return the updated donation.
		return ProjectDonationMapper.mapToDetails(donation);
	}

}
