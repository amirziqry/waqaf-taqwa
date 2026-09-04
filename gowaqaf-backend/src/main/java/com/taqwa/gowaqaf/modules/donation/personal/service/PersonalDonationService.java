package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationRequest;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSumFilter;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface PersonalDonationService {

	PaymentUrlResponse createDonation(AccountUserDetails principal, PersonalDonationRequest dto);

	PersonalDonationDetails getPaymentStatus(UUID donationId, UUID personalId);

	Page<PersonalDonationDetails> getAllDonationDetailsByUser(UUID personalId, Pageable pageable);

	PersonalDonationSum getDonationSumByUser(UUID id, PersonalDonationSumFilter filter);

	PersonalCollectionSum getCollectionSum(LocalDate startDate, LocalDate endDate);

	void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate);

}
