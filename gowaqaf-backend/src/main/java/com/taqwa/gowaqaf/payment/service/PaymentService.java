package com.taqwa.gowaqaf.payment.service;

import com.taqwa.gowaqaf.payment.dto.CollectionCreateRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;

public interface PaymentService {

	String createPaymentCollection(CollectionCreateRequest request);

	PaymentUrlResponse createPaymentBill(PaymentRequest request);

}
