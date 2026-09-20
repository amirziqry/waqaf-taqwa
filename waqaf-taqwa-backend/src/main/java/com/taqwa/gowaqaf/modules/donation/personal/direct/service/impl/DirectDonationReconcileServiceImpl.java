package com.taqwa.gowaqaf.modules.donation.personal.direct.service.impl;

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
import com.taqwa.gowaqaf.modules.donation.personal.direct.service.DirectDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * Fallback for case Webhook token fails to update a donation.
 * <p>
 */

@Service
@RequiredArgsConstructor
public class DirectDonationReconcileServiceImpl implements DirectDonationReconcileService {

	private final NexGenClient nexGenClient;
	private final PersonalDonationRepository repository;
	private final TransactionRepository transactionRepository;
	private final UserService userService;

	@Value("${nexgen.collection.personal}")
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
		}

		repository.saveAndFlush(donation);
	}

	private void reconstructDonationFromBilling(NexGenBillingObject billing) {
		// Get user id.
		UUID personalId = getPersonalIdFromBilling(billing);

		// Create & save donation into DB.
		Transaction transaction = createDonationFromBilling(billing);
		PersonalDonation personalDonation = createPersonalDonation(transaction);

		reconstructDonation(personalId, personalDonation);
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

	private PersonalDonation createPersonalDonation(Transaction transaction) {
		PersonalDonation personalDonation = new PersonalDonation();

		personalDonation.setTransaction(transaction);
		personalDonation.setAccount(null);

		return personalDonation;
	}

	private void reconstructDonation(UUID personalId, PersonalDonation personalDonation) {
		Account user = userService.getUserById(personalId);

		Transaction transaction = transactionRepository.save(personalDonation.getTransaction());

		personalDonation.setId(transaction.getId());
		personalDonation.setTransaction(transaction);
		personalDonation.setAccount(user);

		repository.save(personalDonation);
	}

}
