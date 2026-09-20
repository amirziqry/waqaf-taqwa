package com.taqwa.gowaqaf.modules.donation.personal.project.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;

public interface ProjectDonationService {

	PersonalDonationDetails getDonationDetailsById(UUID personalId, UUID donationId);

	PersonalDonationDetails getDonationDetailsByCode(UUID personalId, String billingCode);

}
