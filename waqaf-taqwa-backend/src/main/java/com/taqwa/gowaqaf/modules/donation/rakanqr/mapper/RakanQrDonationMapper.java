package com.taqwa.gowaqaf.modules.donation.rakanqr.mapper;

import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrDonationDetails;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;

public class RakanQrDonationMapper {

	public static RakanQrDonationDetails mapToDetails(RakanQrDonation donation) {
		RakanQrDonationDetails dto = new RakanQrDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getTransaction().getBillingCode());
		dto.setTransactionId(donation.getTransaction().getTransactionId());
		dto.setAmount(donation.getTransaction().getAmount());
		dto.setPaidAt(donation.getTransaction().getPaidAt());
		dto.setStatus(donation.getTransaction().getStatus());
		dto.setRakanQrCode(donation.getRakanQr().getCode());

		return dto;
	}

}
