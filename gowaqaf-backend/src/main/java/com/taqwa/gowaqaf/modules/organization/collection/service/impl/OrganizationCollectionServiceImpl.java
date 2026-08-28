package com.taqwa.gowaqaf.modules.organization.collection.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.agent.service.RakanQrDonationService;
import com.taqwa.gowaqaf.modules.donation.merchant.service.MerchantDonationService;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalCollectionSum;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSum;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSumFilter;
import com.taqwa.gowaqaf.modules.organization.collection.service.OrganizationCollectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationCollectionServiceImpl implements OrganizationCollectionService {

	private final MerchantDonationService merchantDonationService;
	private final PersonalDonationService personalDonationService;
	private final RakanQrDonationService rakanQrDonationService;

	@Override
	public OrganizationCollectionSum getAllCollectionSum(OrganizationCollectionSumFilter filter) {

		return getAllCollectionSum(filter.getStartDate(), filter.getEndDate());
	}

	@Override
	public OrganizationCollectionSum getAllCollectionSum(LocalDate startDate, LocalDate endDate) {
		PersonalCollectionSum personal = personalDonationService.getCollectionSum(startDate, endDate);
		BigDecimal merchant = merchantDonationService.getCollectionSum(startDate, endDate);
		BigDecimal rakanQr = rakanQrDonationService.getCollectionSum(startDate, endDate);

		BigDecimal total = personal.directTotal().add(personal.recurringTotal()).add(personal.projectTotal())
				.add(merchant).add(rakanQr);

		return new OrganizationCollectionSum(personal.directTotal(), personal.recurringTotal(), personal.projectTotal(),
				merchant, rakanQr, total);
	}

}
