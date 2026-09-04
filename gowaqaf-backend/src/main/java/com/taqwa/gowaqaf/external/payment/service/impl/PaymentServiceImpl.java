package com.taqwa.gowaqaf.external.payment.service.impl;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.external.payment.client.nexgen.client.NexGenPaymentClient;
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

	private final NexGenPaymentClient nexGenPaymentClient;

	@Override
	public PaymentUrlResponse createPaymentBill(PaymentRequest request) {
		// Build NexGen billing request body.
		NexGenCreateBillingRequest billingRequest = buildBillingRequest(request);

		// NexGen API call.
		NexGenBillingResponse billingResponse = nexGenPaymentClient.createBilling(request.getCollectionCode(),
				billingRequest);

		// Build payment URL response;
		PaymentUrlResponse paymentUrlResponse = buildPaymentUrlResponse(billingResponse);

		return paymentUrlResponse;
	}

	private NexGenCreateBillingRequest buildBillingRequest(PaymentRequest request) {
		NexGenCreateBillingRequest billingRequest = new NexGenCreateBillingRequest();

		billingRequest.setFieldName(request.getName());
		billingRequest.setFieldEmail(request.getEmail());
		billingRequest.setFieldPhone(request.getPhone());
		billingRequest.setFieldAmount(request.getAmount());
		billingRequest.setFieldPaymentDescription(request.getDescription());
		billingRequest.setFieldDueDate(null);
		billingRequest.setFieldRedirectUrl(request.getRedirectUrl());
		billingRequest.setFieldCallbackUrl(request.getCallbackUrl());

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
