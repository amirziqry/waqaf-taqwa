package com.taqwa.gowaqaf.modules.donation.vendor.service;

import java.util.UUID;

import com.taqwa.gowaqaf.modules.donation.vendor.dto.VendorDonationSum;

public interface VendorDonationService {

	VendorDonationSum getDonationSumById(UUID id);

}
