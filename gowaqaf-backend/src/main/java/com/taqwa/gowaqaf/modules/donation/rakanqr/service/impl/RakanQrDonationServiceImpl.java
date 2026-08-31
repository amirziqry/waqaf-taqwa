package com.taqwa.gowaqaf.modules.donation.rakanqr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.exception.code.ErrorCode;
import com.taqwa.gowaqaf.exception.custom.BadRequestException;
import com.taqwa.gowaqaf.exception.custom.ResourceNotFoundException;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationFilter;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationSum;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.service.RakanQrDonationService;
import com.taqwa.gowaqaf.modules.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.security.account.AccountType;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RakanQrDonationServiceImpl implements RakanQrDonationService {

	private final RakanQrRepository agentRepository;
	private final RakanQrDonationRepository donationRepository;

	@Override
	public RakanQrDonationSum getDonationSumByUser(Authentication authentication, RakanQrDonationFilter filter) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		RakanQr agent = null;

		if (principal.getAccountType() == AccountType.MERCHANT) {
			agent = agentRepository.findByMerchant_Username(principal.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Merchant account not found"));

		} else if (principal.getAccountType() == AccountType.PERSONAL) {
			agent = agentRepository.findByPersonal_Username(principal.getUsername())
					.orElseThrow(() -> new ResourceNotFoundException(ErrorCode.A001, "Personal account not found"));

		} else {
			throw new BadRequestException(ErrorCode.A001, "Account type not allowed.");
		}

		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;

		if (filter.getStartDate() != null)
			startDateTime = filter.getStartDate().atStartOfDay();

		if (filter.getEndDate() != null)
			endDateTime = filter.getEndDate().atTime(LocalTime.MAX);

		BigDecimal sum = donationRepository.sumPaidDonationsByAgent(agent.getId(), startDateTime, endDateTime);

		return new RakanQrDonationSum(sum);
	}

	@Override
	public BigDecimal getCollectionSum(LocalDate startDate, LocalDate endDate) {
		LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
		LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

		BigDecimal total = donationRepository.sumAllPaidDonations(startDateTime, endDateTime);

		return total;
	}

}
