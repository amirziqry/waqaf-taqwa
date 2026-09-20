package com.taqwa.gowaqaf.modules.donation.rakanqr.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

public interface RakanQrDonationService {

	RakanQrDonationDetails getDonationDetailsById(UUID donationId);

	RakanQrCollection getDonationCollectionByUser(CustomUserDetails principal, RakanQrDonationFilter filter);

	RakanQrCollection getDonationCollectionByUser(UUID rakanQrId, LocalDate startDate, LocalDate endDate);

	Page<RakanQrDonationDetails> getAllDonationDetailsByUser(CustomUserDetails principal, Pageable pageable);

	Page<RakanQrDonationDetails> getAllDonationDetailsByUser(UUID rakanQrId, Pageable pageable);

	BigDecimal getTotalCollectionFromRakanQr(LocalDate startDate, LocalDate endDate);

	List<RakanQrDonationDetails> getDonationDetailsListByUser(UUID rakanQrId, Pageable pageable);

	void reconstructDonation(UUID rakanQrId, RakanQrDonation rakanQrDonation);

}
