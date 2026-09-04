package com.taqwa.gowaqaf.modules.donation.personal.mapper;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public class PersonalDonationMapper {

	public static PersonalDonationDetails mapToDetails(PersonalDonation donation) {
		PersonalDonationDetails dto = new PersonalDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getBillingCode());
		dto.setTransactionId(donation.getTransactionId());
		dto.setAmount(donation.getAmount());
		dto.setPaidAt(donation.getPaidAt());
		dto.setStatus(donation.getStatus());
		dto.setReceiptHashId(donation.getReceiptHashId());

		return dto;
	}

}
