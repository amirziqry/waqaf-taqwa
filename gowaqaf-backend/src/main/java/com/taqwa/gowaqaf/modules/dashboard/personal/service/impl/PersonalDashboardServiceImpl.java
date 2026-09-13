package com.taqwa.gowaqaf.modules.dashboard.personal.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taqwa.gowaqaf.modules.dashboard.personal.dto.PersonalDashboard;
import com.taqwa.gowaqaf.modules.dashboard.personal.service.PersonalDashboardService;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationDetails;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.service.PersonalDonationService;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.about.service.OrganizationAboutService;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.service.CampaignService;
import com.taqwa.gowaqaf.modules.organization.content.news.dto.NewsDetails;
import com.taqwa.gowaqaf.modules.organization.content.news.service.NewsService;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.service.ProjectService;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonalDashboardServiceImpl implements PersonalDashboardService {

	private final PersonalDonationService donationService;
	private final PersonalService personalService;
	private final OrganizationAboutService profileService;
	private final ProjectService projectService;
	private final NewsService newsService;
	private final CampaignService campaignService;

	@Override
	public PersonalDashboard getDashboardByUser(AccountUserDetails principal) {
		PersonalDashboard dashboard = new PersonalDashboard();

		dashboard.setContributions(getPersonalDonationTotal(principal.getId()));
		dashboard.setAccountInfo(getPersonalAccountInfo(principal));
		dashboard.setOrgAbout(getOrganizationProfile());
		dashboard.setProjects(getProjectDetailsList());
		dashboard.setNews(getNewsDetailsList());
		dashboard.setCampaigns(getCampaignsDetailsList());
		dashboard.setDonations(getAllPersonalDonationDetails(principal.getId()));

		return dashboard;
	}

	private PersonalDonationSum getPersonalDonationTotal(UUID id) {
		LocalDate today = LocalDate.now();
		LocalDate startDate = today.withDayOfMonth(1);
		LocalDate endDate = today.plusMonths(1).withDayOfMonth(1);

		return donationService.getDonationSumByUser(id, startDate, endDate);
	}

	private PersonalAccountInfo getPersonalAccountInfo(AccountUserDetails principal) {
		return personalService.getAccountByUser(principal);
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

	private List<PersonalDonationDetails> getAllPersonalDonationDetails(UUID personalId) {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("transaction.createdAt"), Sort.Order.desc("id")));

		List<PersonalDonationDetails> donations = donationService.getDonationDetailsListByUser(personalId, pageable);

		return donations;
	}

}
