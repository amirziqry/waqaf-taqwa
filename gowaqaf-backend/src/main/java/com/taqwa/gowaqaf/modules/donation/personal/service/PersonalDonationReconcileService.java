package com.taqwa.gowaqaf.modules.donation.personal.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;

public interface PersonalDonationReconcileService {

	void reconcile(String billingCode);

	PersonalDonationDetails getDonationDetailsById(UUID personalID, UUID donationId);

	PersonalDonationDetails getDonationDetailsByCode(UUID personalId, String code);

}
