package com.taqwa.gowaqaf.modules.donation.project.mapper;

import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;

public class ProjectDonationMapper {

	public static ProjectDonationDetails mapToDetails(ProjectDonation donation) {
		ProjectDonationDetails dto = new ProjectDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getTransaction().getBillingCode());
		dto.setTransactionId(donation.getTransaction().getTransactionId());
		dto.setAmount(donation.getTransaction().getAmount());
		dto.setPaidAt(donation.getTransaction().getPaidAt());
		dto.setStatus(donation.getTransaction().getStatus());
		dto.setReceiptHashId(donation.getReceiptHashId());
		dto.setProjectId(donation.getProject().getId());
		dto.setProjectName(donation.getProject().getName());

		return dto;
	}

}
