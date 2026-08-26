package com.taqwa.gowaqaf.modules.donation.organization.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSum;
import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSumFilter;
import com.taqwa.gowaqaf.modules.donation.organization.service.OrganizationDonationService;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationDonationServiceImpl implements OrganizationDonationService {

	private final MerchantDonationRepository merchantDonationRepository;
	private final PersonalDonationRepository personalDonationRepository;

	@Override
	public OrganizationDonationSum getAllDonationSum(OrganizationDonationSumFilter filter) {

		LocalDateTime startDateTime = null;
		LocalDateTime endDateTime = null;

		if (filter.getStartDate() != null)
			startDateTime = filter.getStartDate().atStartOfDay();

		if (filter.getEndDate() != null)
			endDateTime = filter.getEndDate().atTime(LocalTime.MAX);

		BigDecimal donatorTotal = personalDonationRepository.sumAllPaidDonations(startDateTime, endDateTime);

		BigDecimal vendorTotal = merchantDonationRepository.sumAllPaidDonations(startDateTime, endDateTime);

		BigDecimal total = donatorTotal.add(vendorTotal);

		return new OrganizationDonationSum(donatorTotal, vendorTotal, total);
	}

}
