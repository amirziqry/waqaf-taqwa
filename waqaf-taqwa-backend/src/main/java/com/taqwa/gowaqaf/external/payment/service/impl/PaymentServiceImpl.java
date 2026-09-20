package com.taqwa.gowaqaf.external.payment.service.impl;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.external.payment.client.nexgen.client.NexGenClient;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenBillingResponse;
import com.taqwa.gowaqaf.external.payment.client.nexgen.dto.billing.NexGenCreateBillingRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.external.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

/**
 * Payment service implementation.
 *
 * Handles application-level payment operations and delegates communication with
 * the NexGen Payment API to NexGenPaymentClient.
 */

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final NexGenClient nexGenClient;

	/**
	 * TODO: Payment exists, but donation doesn't > create a new donation (external
	 * ref/value need filling)
	 * 
	 * Webhook > token fail > billing code not found > get billing code from nexgen
	 * (verify) > reconstruct donation including ref/val
	 */
	/**
	 * TODO: Donation exist, but webhook token fail
	 * 
	 * Get billing from nexgen, update
	 */
	/**
	 * TODO: Donation exists, payment request failed > rollback / mark as cancel
	 */
	/**
	 * TODO: Donation fail, but billing request succeed
	 * 
	 * Make sure backend strictly not response user with the payment url.
	 */
	/**
	 * TODO: Payment succeeds, webhook fails > scheduler, custom endpoint to refresh
	 * status
	 * 
	 * - Scheduler ~2-5mins to update all unpaid donations - Custom endpoint to
	 * refresh status (get billing from nexgen and update)
	 */
	// statu

	@Override
	public PaymentUrlResponse createPaymentBill(PaymentRequest request) {
		// Build NexGen billing request body.
		NexGenCreateBillingRequest billingRequest = buildBillingRequest(request);

		// Pass to NexGen client service.
		NexGenBillingResponse billingResponse = nexGenClient.createBilling(request.getCollectionCode(), billingRequest);

		// Build & return payment URL response;
		return buildPaymentUrlResponse(billingResponse);
	}

	private NexGenCreateBillingRequest buildBillingRequest(PaymentRequest request) {
		NexGenCreateBillingRequest billingRequest = new NexGenCreateBillingRequest();

		// Set billing details.
		billingRequest.setFieldName(request.getName());
		billingRequest.setFieldEmail(request.getEmail());
		billingRequest.setFieldPhone(request.getPhone());
		billingRequest.setFieldAmount(request.getAmount());
		billingRequest.setFieldPaymentDescription(request.getDescription());
		billingRequest.setFieldDueDate(null);
		billingRequest.setFieldRedirectUrl(request.getRedirectUrl());
		billingRequest.setFieldCallbackUrl(request.getCallbackUrl());

		// Set external reference values, if any.
		billingRequest.setFieldExternalReferenceLabel1(request.getReferenceLabel1());
		billingRequest.setFieldExternalReferenceValue1(request.getReferenceValue1());
		billingRequest.setFieldExternalReferenceLabel2(request.getReferenceLabel2());
		billingRequest.setFieldExternalReferenceValue2(request.getReferenceValue2());
		billingRequest.setFieldExternalReferenceLabel3(request.getReferenceLabel3());
		billingRequest.setFieldExternalReferenceValue3(request.getReferenceValue3());
		billingRequest.setFieldExternalReferenceLabel4(request.getReferenceLabel4());
		billingRequest.setFieldExternalReferenceValue4(request.getReferenceValue4());

		return billingRequest;
	}

	private PaymentUrlResponse buildPaymentUrlResponse(NexGenBillingResponse billingResponse) {
		PaymentUrlResponse paymentUrlResponse = new PaymentUrlResponse();

		paymentUrlResponse.setBillingCode(billingResponse.getCode());
		paymentUrlResponse.setAmount(billingResponse.getAmount());
		paymentUrlResponse.setStatus(billingResponse.getStatus().toUpperCase());
		paymentUrlResponse.setPaymentUrl(billingResponse.getPaymentUrl());

		return paymentUrlResponse;
	}

}
