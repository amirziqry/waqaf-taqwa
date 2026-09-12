package com.taqwa.gowaqaf.modules.dashboard.personal.dto;

import java.util.List;

import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDashboard {

	private PersonalDonationSum contributions;

	private PersonalAccountInfo accountInfo;

	private OrgAboutDetails orgAbout;

	private List<ProjectDetails> projects;

	private List<NewsDetails> news;

	private List<CampaignDetails> campaigns;

	private List<PersonalDonationDetails> donations;

}
