package com.taqwa.gowaqaf.modules.donation.personal.mapper;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public class PersonalDonationMapper {

	public static PersonalDonationDetails mapToDetails(PersonalDonation donation) {
		PersonalDonationDetails dto = new PersonalDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getDonation().getBillingCode());
		dto.setTransactionId(donation.getDonation().getTransactionId());
		dto.setAmount(donation.getDonation().getAmount());
		dto.setPaidAt(donation.getDonation().getPaidAt());
		dto.setStatus(donation.getDonation().getStatus());
		dto.setReceiptHashId(donation.getReceiptHashId());

		return dto;
	}

}
