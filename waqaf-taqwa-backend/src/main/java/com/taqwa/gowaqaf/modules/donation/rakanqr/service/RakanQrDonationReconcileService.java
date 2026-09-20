package com.taqwa.gowaqaf.modules.donation.rakanqr.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;

public interface RakanQrDonationReconcileService {

	void reconcile(String billingCode);

	RakanQrDonationDetails getDonationDetailsById(UUID donationId);

	RakanQrDonationDetails getDonationDetailsByCode(String billingCode);

}
