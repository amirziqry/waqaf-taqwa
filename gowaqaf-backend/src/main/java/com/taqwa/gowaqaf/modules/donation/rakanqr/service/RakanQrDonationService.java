package com.taqwa.gowaqaf.modules.donation.rakanqr.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationRequest;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface RakanQrDonationService {

	PaymentUrlResponse createDonation(String agentCode, RakanQrDonationRequest request);

	RakanQrDonationDetails getDonationDetailsById(UUID donationId);

	RakanQrCollection getDonationCollectionByUser(AccountUserDetails principal, RakanQrDonationFilter filter);

	RakanQrCollection getDonationCollectionByUser(UUID rakanQrId, LocalDate startDate, LocalDate endDate);

	Page<RakanQrDonationDetails> getAllDonationDetailsByUser(AccountUserDetails principal, Pageable pageable);

	Page<RakanQrDonationDetails> getAllDonationDetailsByUser(UUID rakanQrId, Pageable pageable);

	BigDecimal getTotalCollectionFromRakanQr(LocalDate startDate, LocalDate endDate);

	void processWebhook(String token, String code, String status, BigDecimal amount, String transactionId,
			String orderId, LocalDateTime transactionDate);

	List<RakanQrDonationDetails> getDonationDetailsListByUser(UUID rakanQrId, Pageable pageable);

}
