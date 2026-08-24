package com.taqwa.gowaqaf.payment.service.impl;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.payment.dto.CollectionCreateRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenBillingResponse;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenCollectionResponse;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenCreateBillingRequest;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenCreateCollectionRequest;
import com.taqwa.gowaqaf.payment.gateway.nexgen.service.NexGenPaymentClient;
import com.taqwa.gowaqaf.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final NexGenPaymentClient nexGenPaymentClient;

	@Override
	public String createPaymentCollection(CollectionCreateRequest request) {
		NexGenCreateCollectionRequest dto = new NexGenCreateCollectionRequest();

		dto.setFieldName(request.getName());
		dto.setFieldDescription(request.getDescription());
		dto.setFieldStatus(request.getStatus().toString().toLowerCase());

		// TODO: API Integration
		NexGenCollectionResponse response = nexGenPaymentClient.createCollection(dto);

		String collectionCode = response.getCode();

		return collectionCode;
	}

	@Override
	public PaymentUrlResponse createPaymentBill(PaymentRequest request) {
		NexGenCreateBillingRequest billingRequest = new NexGenCreateBillingRequest();
		billingRequest.setFieldName(request.getName());
		billingRequest.setFieldAmount(request.getAmount());
		billingRequest.setFieldRedirectUrl(request.getRedirectUrl());
		billingRequest.setFieldCallbackUrl(request.getCallbackUrl());

		NexGenBillingResponse billingResponse = nexGenPaymentClient.createBilling(request.getCollectionCode(),
				billingRequest);

		// TODO: API Integration
		PaymentUrlResponse paymentUrlResponse = new PaymentUrlResponse();

		paymentUrlResponse.setBillingCode(billingResponse.getCode());
		paymentUrlResponse.setStatus(billingResponse.getStatus().toUpperCase());
		paymentUrlResponse.setPaymentUrl(billingResponse.getPaymentUrl());

		return paymentUrlResponse;
	}

}
