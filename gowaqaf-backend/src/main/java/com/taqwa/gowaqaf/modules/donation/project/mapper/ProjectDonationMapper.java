package com.taqwa.gowaqaf.modules.donation.project.mapper;

import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationDetails;

public class ProjectDonationMapper {

	public static ProjectDonationDetails mapToDetails(PersonalDonation donation) {
		ProjectDonationDetails dto = new ProjectDonationDetails();

		dto.setId(donation.getId());
		dto.setBillingCode(donation.getBillingCode());
		dto.setAmount(donation.getAmount());
		dto.setPaidAt(donation.getPaidAt());
		dto.setStatus(donation.getStatus());
		dto.setProjectId(donation.getProject().getId());
		dto.setProjectName(donation.getProject().getName());

		return dto;
	}

}
