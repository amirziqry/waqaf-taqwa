package com.taqwa.gowaqaf.modules.donation.project.mapper;

import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;

public class ProjectDonationMapper {

	public static ProjectDonationDetails mapToDetails(ProjectDonation donation) {
		ProjectDonationDetails dto = new ProjectDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getDonation().getBillingCode());
		dto.setTransactionId(donation.getDonation().getTransactionId());
		dto.setAmount(donation.getDonation().getAmount());
		dto.setPaidAt(donation.getDonation().getPaidAt());
		dto.setStatus(donation.getDonation().getStatus());
		dto.setReceiptHashId(donation.getReceiptHashId());
		dto.setProjectId(donation.getProject().getId());
		dto.setProjectName(donation.getProject().getName());

		return dto;
	}

}
