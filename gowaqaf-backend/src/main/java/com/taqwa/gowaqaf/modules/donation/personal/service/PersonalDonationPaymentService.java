package com.taqwa.gowaqaf.modules.donation.personal.service;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface PersonalDonationPaymentService {

	PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto);

}
