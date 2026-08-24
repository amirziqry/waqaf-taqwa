package com.taqwa.gowaqaf.payment.gateway.nexgen.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenBillingResponse;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenCollectionResponse;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenCreateBillingRequest;
import com.taqwa.gowaqaf.payment.gateway.nexgen.dto.NexGenCreateCollectionRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NexGenPaymentClient {

	public NexGenCollectionResponse createCollection(NexGenCreateCollectionRequest request) {
		// TODO: API Integration
		NexGenCollectionResponse collection = generateCollection(request);

		return collection;
	}

	private NexGenCollectionResponse generateCollection(NexGenCreateCollectionRequest request) {
		NexGenCollectionResponse collection = new NexGenCollectionResponse();

		collection.setCode("COL" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
		collection.setName(request.getFieldName());
		collection.setDescription(request.getFieldDescription());
		collection.setStatus(request.getFieldStatus());

		return collection;
	}

	public NexGenBillingResponse createBilling(String collectionCode, NexGenCreateBillingRequest request) {
		// TODO: API Integration
		NexGenBillingResponse billing = generateBill(collectionCode, request);

		return billing;
	}

	private NexGenBillingResponse generateBill(String collectionCode, NexGenCreateBillingRequest request) {
		NexGenBillingResponse bill = new NexGenBillingResponse();

		bill.setCode("BIL" + UUID.randomUUID().toString().replace("-", "").substring(0, 8));
		bill.setStatus("unpaid");
		bill.setAmount(request.getFieldAmount());
		bill.setPayerName(request.getFieldName());
		bill.setCallbackUrl("http://localhost:8080/test-webhook");
		bill.setPaymentUrl("https://system-nexgen.test/test-payment");

		return bill;
	}

}
