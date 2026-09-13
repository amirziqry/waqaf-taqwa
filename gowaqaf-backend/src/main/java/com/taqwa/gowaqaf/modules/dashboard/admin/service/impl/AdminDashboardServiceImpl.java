package com.taqwa.gowaqaf.modules.dashboard.admin.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.dashboard.admin.dto.AdminDashboard;
import com.taqwa.gowaqaf.modules.dashboard.admin.service.AdminDashboardService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.about.service.OrganizationAboutService;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrgCollectionInfo;
import com.taqwa.gowaqaf.modules.organization.collection.service.DonationCollectionService;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.service.CampaignService;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.service.NewsService;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private final DonationCollectionService collectionService;
	private final OrganizationAboutService profileService;
	private final ProjectService projectService;
	private final NewsService newsService;
	private final CampaignService campaignService;
	private final RakanQrService agentService;

	@Override
	public AdminDashboard getDashboard() {
		AdminDashboard dashboard = new AdminDashboard();

		dashboard.setCollections(getDonationSummary());
		dashboard.setOrgAbout(getOrganizationProfile());
		dashboard.setProjects(getProjectDetailsList());
		dashboard.setNews(getNewsDetailsList());
		dashboard.setCampaigns(getCampaignsDetailsList());
		dashboard.setRakanQrs(getRakanQrInfoList());

		return dashboard;
	}

	private OrgCollectionInfo getDonationSummary() {
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.withDayOfMonth(1);
		LocalDate endDate = today.plusMonths(1).withDayOfMonth(1);

		return collectionService.getDonationCollectionSum(startDate, endDate);
	}

	private OrgAboutDetails getOrganizationProfile() {
		return profileService.getAbout();
	}

	private List<ProjectDetails> getProjectDetailsList() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));

		List<ProjectDetails> projects = projectService.getProjectsDetailsList(pageable);

		return projects;
	}

	private List<NewsDetails> getNewsDetailsList() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));

		List<NewsDetails> news = newsService.getNewsList(pageable);

		return news;
	}

	private List<CampaignDetails> getCampaignsDetailsList() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id")));

		List<CampaignDetails> campaigns = campaignService.getCampaignsList(pageable);

		return campaigns;
	}

	private List<RakanQrInfo> getRakanQrInfoList() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));

		return agentService.getRakanQrInfoList(pageable);
	}

}
