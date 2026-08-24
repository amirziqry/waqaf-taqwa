package com.taqwa.gowaqaf.modules.donation.organization.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.donation.donator.repository.DonatorDonationRepository;
import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSum;
import com.taqwa.gowaqaf.modules.donation.organization.service.OrganizationDonationService;
import com.taqwa.gowaqaf.modules.donation.vendor.repository.VendorDonationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationDonationServiceImpl implements OrganizationDonationService {

	private final VendorDonationRepository vendorDonationRepository;
	private final DonatorDonationRepository donatorDonationRepository;

	@Override
	public OrganizationDonationSum getAllDonationSum() {
		BigDecimal donatorTotal = donatorDonationRepository.sumAllPaidDonations();

		BigDecimal vendorTotal = vendorDonationRepository.sumAllPaidDonations();

		BigDecimal total = donatorTotal.add(vendorTotal);

		return new OrganizationDonationSum(donatorTotal, vendorTotal, total);
	}

}
