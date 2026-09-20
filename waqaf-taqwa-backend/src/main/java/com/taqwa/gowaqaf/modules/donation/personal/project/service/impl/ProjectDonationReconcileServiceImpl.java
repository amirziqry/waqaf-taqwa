package com.taqwa.gowaqaf.modules.donation.personal.project.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.external.payment.client.nexgen.client.NexGenClient;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenBillingObject;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.project.service.ProjectDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectDonationReconcileServiceImpl implements ProjectDonationReconcileService {

	private final NexGenClient nexGenClient;
	private final PersonalDonationRepository repository;
	private final TransactionRepository transactionRepository;
	private final ProjectService projectService;
	private final UserService userService;

	@Value("${nexgen.collection.project}")
	private String collectionCode;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reconcile(String billingCode) {
		// Get the authoritative billing data from NexGen.
		NexGenBillingObject billing = nexGenClient.getBilling(collectionCode, billingCode);

		// Try find the donation with respective billing code locally.
		PersonalDonation donation = repository.findByTransaction_BillingCode(billingCode).orElse(null);

		// If donation exists -> synchronize information with NexGen's data.
		if (donation != null)
			updateDonationFromBilling(donation, billing);

		// If donation missing -> reconstruct information with NexGen's data.
		if (donation == null)
			reconstructDonationFromBilling(billing);

	}

	private void updateDonationFromBilling(PersonalDonation donation, NexGenBillingObject billing) {
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
		PersonalDonation donation = createProjectDonation(transaction);

		reconstructDonation(userId, projectId, donation);
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

	private PersonalDonation createProjectDonation(Transaction transaction) {
		PersonalDonation projectDonation = new PersonalDonation();

		projectDonation.setTransaction(transaction);
		projectDonation.setProject(null);
		projectDonation.setAccount(null);

		return projectDonation;
	}

	private void reconstructDonation(UUID personalId, UUID projectId, PersonalDonation donation) {
		Account account = userService.getUserById(personalId);
		Project project = projectService.getProjectById(projectId);

		Transaction transaction = transactionRepository.save(donation.getTransaction());

		donation.setId(transaction.getId());
		donation.setTransaction(transaction);
		donation.setProject(project);
		donation.setAccount(account);

		PersonalDonation saved = repository.save(donation);

		if (saved.getTransaction().getStatus() != PaymentStatus.PAID)
			projectService.updateProjectCollectedAmountById(saved.getProject().getId(),
					saved.getTransaction().getAmount());
	}

}
