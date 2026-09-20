package com.taqwa.gowaqaf.modules.donation.personal.direct.service;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.direct.dto.DirectDonationRequest;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface DirectDonationPaymentService {

	PaymentUrlResponse createDonation(CustomUserDetails principal, DirectDonationRequest dto);

}
