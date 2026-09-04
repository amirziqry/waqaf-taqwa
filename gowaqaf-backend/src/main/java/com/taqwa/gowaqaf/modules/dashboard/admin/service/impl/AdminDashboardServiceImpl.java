package com.taqwa.gowaqaf.modules.dashboard.admin.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.dashboard.admin.dto.AdminDashboard;
import com.taqwa.gowaqaf.modules.dashboard.admin.service.AdminDashboardService;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrWithSum;
import com.taqwa.gowaqaf.modules.feature.rakanqr.service.RakanQrService;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSum;
import com.taqwa.gowaqaf.modules.organization.collection.service.OrganizationCollectionService;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.service.CampaignService;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.service.NewsService;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileDetails;
import com.taqwa.gowaqaf.modules.organization.profile.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private final OrganizationCollectionService collectionService;
	private final ProjectService projectService;
	private final NewsService newsService;
	private final CampaignService campaignService;
	private final OrganizationService profileService;
	private final RakanQrService agentService;

	@Override
	public AdminDashboard getDashboard() {
		AdminDashboard dashboard = new AdminDashboard();

		dashboard.setCollectionSum(getDonationSummary());
		dashboard.setProjects(getProjects());
		dashboard.setNews(getNews());
		dashboard.setCampaigns(getCampaigns());
		dashboard.setOrganizationProfile(getOrganizationProfile());
		dashboard.setRakanQrSummary(getRakanQrSummary());

		return dashboard;
	}

	private OrganizationCollectionSum getDonationSummary() {
		LocalDate today = LocalDate.now();

		LocalDate startDate = today.withDayOfMonth(1);
		LocalDate endDate = today.plusMonths(1).withDayOfMonth(1);

		return collectionService.getAllCollectionSum(startDate, endDate);
	}

	private List<ProjectDetails> getProjects() {
		return projectService.getAllProjectsDetails();
	}

	private List<NewsDetails> getNews() {
		return newsService.getAllNews();
	}

	private List<CampaignDetails> getCampaigns() {
		return campaignService.getAllCampaigns();
	}

	private OrganizationProfileDetails getOrganizationProfile() {
		return profileService.getProfile();
	}

	private List<RakanQrWithSum> getRakanQrSummary() {
		LocalDate today = LocalDate.now();

		LocalDate startDate = today.withDayOfMonth(1);
		LocalDate endDate = today.plusMonths(1).withDayOfMonth(1);

		return agentService.getAllRakanQrWithSum(startDate, endDate);
	}

}
