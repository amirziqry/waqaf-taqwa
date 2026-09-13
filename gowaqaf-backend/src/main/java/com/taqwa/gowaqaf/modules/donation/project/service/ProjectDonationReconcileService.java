package com.taqwa.gowaqaf.modules.donation.project.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

public interface ProjectDonationReconcileService {

	void reconcile(String billingCode);

	ProjectDonationDetails getDonationDetailsById(AccountUserDetails principal, UUID donationId);

	ProjectDonationDetails getDonationDetailsByCode(AccountUserDetails principal, String billingCode);

}
