package com.taqwa.gowaqaf.modules.donation.rakanqr.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.webhook.NexGenWebhookPayload;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationReconcileService;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationWebhookService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationWebhookServiceImpl implements RakanQrDonationWebhookService {

	private final RakanQrDonationRepository repository;
	private final RakanQrDonationReconcileService reconcileService;
	private final RakanQrService rakanQrService;

	@Override
	@Transactional
	public void handleWebhook(String token, NexGenWebhookPayload payload) {
		try {
			processWebhook(token, payload);
		} catch (Exception e) {
			reconcileService.reconcile(payload.getCode());
		}
	}

	private void processWebhook(String token, NexGenWebhookPayload payload) {
		// Get donation from DB.
		RakanQrDonation donation = repository.findByTransaction_WebhookToken(token)
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.WHK001, "Donation not found."));

		// Validate billing code > reconcile for mismatch.
		if (!donation.getTransaction().getBillingCode().equals(payload.getCode()))
			throw new BadRequestException(ErrorCode.WHK002, "Billing code does not match");

		// Validate amount > reconcile for mismatch.
		if (donation.getTransaction().getAmount().compareTo(payload.getAmount()) != 0)
			throw new BadRequestException(ErrorCode.WHK003, "Amount does not match");

		PaymentStatus previousStatus = donation.getTransaction().getStatus();

		// Update payment status.
		switch (payload.getStatus().toLowerCase()) {
		case "paid" -> donation.getTransaction().setStatus(PaymentStatus.PAID);
		case "pending" -> donation.getTransaction().setStatus(PaymentStatus.PENDING);
		case "unpaid" -> donation.getTransaction().setStatus(PaymentStatus.UNPAID);
		default -> donation.getTransaction().setStatus(PaymentStatus.EXPIRED);
		}

		// Update donation details if status paid.
		if (donation.getTransaction().getStatus() == PaymentStatus.PAID && previousStatus != PaymentStatus.PAID) {
			String transactionId = payload.getPaymentMethodDetail().getTransactionId();
			String orderId = payload.getPaymentMethodDetail().getOrderId();
			LocalDateTime transactionDate = payload.getPaymentMethodDetail().getTransactionDate();

			// Save transaction/order id.
			donation.getTransaction().setTransactionId(transactionId != null ? transactionId : orderId);

			// Save transaction date time.
			donation.getTransaction().setPaidAt(transactionDate);

			// Nullify webhook token.
			donation.getTransaction().setWebhookToken(null);

			donation.getTransaction().setPaymentMethod(payload.getPaymentMethodAccepted());

			if (previousStatus != PaymentStatus.PAID)
				rakanQrService.updateRakanQrCollectedAmountById(donation.getRakanQr().getId(),
						donation.getTransaction().getAmount());
		}
	}
}
