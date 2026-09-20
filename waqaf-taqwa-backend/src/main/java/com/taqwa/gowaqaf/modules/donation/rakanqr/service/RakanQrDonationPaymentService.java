package com.taqwa.gowaqaf.modules.donation.rakanqr.service;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationRequest;

public interface RakanQrDonationPaymentService {

	PaymentUrlResponse createDonation(String agentCode, RakanQrDonationRequest request);

}
