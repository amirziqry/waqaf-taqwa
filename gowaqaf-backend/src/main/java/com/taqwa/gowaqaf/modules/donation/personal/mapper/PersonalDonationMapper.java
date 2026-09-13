package com.taqwa.gowaqaf.modules.donation.personal.mapper;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;

public class PersonalDonationMapper {

	public static PersonalDonationDetails mapToDetails(PersonalDonation donation) {
		PersonalDonationDetails dto = new PersonalDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getTransaction().getBillingCode());
		dto.setTransactionId(donation.getTransaction().getTransactionId());
		dto.setAmount(donation.getTransaction().getAmount());
		dto.setPaidAt(donation.getTransaction().getPaidAt());
		dto.setStatus(donation.getTransaction().getStatus());
		dto.setReceiptHashId(donation.getReceiptHashId());

		return dto;
	}

}
