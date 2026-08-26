package com.taqwa.gowaqaf.modules.donation.organization.service;

import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSum;
import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSumFilter;

public interface OrganizationDonationService {

	OrganizationDonationSum getAllDonationSum(OrganizationDonationSumFilter filter);

}
