package com.taqwa.gowaqaf.modules.donation.personal.direct.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;


public interface DirectDonationService {

	PersonalDonationDetails getDonationDetailsById(UUID personalID, UUID donationId);

	PersonalDonationDetails getDonationDetailsByCode(UUID personalId, String billingCode);

}
