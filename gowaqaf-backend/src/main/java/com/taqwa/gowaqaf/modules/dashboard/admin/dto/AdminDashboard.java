package com.taqwa.gowaqaf.modules.dashboard.admin.dto;

import java.util.List;

import com.taqwa.gowaqaf.modules.agent.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSum;
import com.taqwa.gowaqaf.modules.organization.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileDetails;
import com.taqwa.gowaqaf.modules.organization.project.dto.ProjectDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboard {

	private OrganizationCollectionSum collectionSum;

	private List<ProjectDetails> projects;

	private List<NewsDetails> news;

	private List<CampaignDetails> campaigns;

	private OrganizationProfileDetails organizationProfile;

	private List<RakanQrWithSum> rakanQrSummary;

}
