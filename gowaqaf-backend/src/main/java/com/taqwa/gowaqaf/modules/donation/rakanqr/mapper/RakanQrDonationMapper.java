package com.taqwa.gowaqaf.modules.donation.rakanqr.mapper;

import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;

public class RakanQrDonationMapper {

	public static RakanQrDonationDetails mapToDetails(RakanQrDonation donation) {
		RakanQrDonationDetails dto = new RakanQrDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getDonation().getBillingCode());
		dto.setTransactionId(donation.getDonation().getTransactionId());
		dto.setAmount(donation.getDonation().getAmount());
		dto.setPaidAt(donation.getDonation().getPaidAt());
		dto.setStatus(donation.getDonation().getStatus());
		dto.setRakanQrCode(donation.getRakanQr().getCode());

		return dto;
	}

}
