package com.taqwa.gowaqaf.external.payment.service;

import com.taqwa.gowaqaf.external.payment.dto.PaymentRequest;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;

public interface PaymentService {

	PaymentUrlResponse createPaymentBill(PaymentRequest request);

}
