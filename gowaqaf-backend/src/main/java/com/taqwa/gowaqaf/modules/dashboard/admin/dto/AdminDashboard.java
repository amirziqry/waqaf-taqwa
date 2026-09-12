package com.taqwa.gowaqaf.modules.dashboard.admin.dto;

import java.util.List;

import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboard {

	private OrgCollectionInfo collections;

	private OrgAboutDetails orgAbout;

	private List<ProjectDetails> projects;

	private List<NewsDetails> news;

	private List<CampaignDetails> campaigns;

	private List<RakanQrInfo> rakanQrs;

}
