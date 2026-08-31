package com.taqwa.gowaqaf.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.merchant.entity.MerchantDonation;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.organization.content.campaign.entity.Campaign;
import com.taqwa.gowaqaf.modules.organization.content.campaign.repository.CampaignRepository;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;
import com.taqwa.gowaqaf.modules.organization.content.news.repository.NewsRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.organization.profile.entity.OrganizationProfile;
import com.taqwa.gowaqaf.modules.organization.profile.repository.OrganizationRepository;
import com.taqwa.gowaqaf.modules.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

public class CommonClass {

	// Mock admin user.
	public static Admin createMockAdmin(AdminRepository repository, AccountInfoRepository identityRepository,
			PasswordEncoder passwordEncoder, String username, String email, Set<Role> roles) {
		Admin test = new Admin();
		AccountInfo info = new AccountInfo();

		test.setUsername(username);
		test.setPassword(passwordEncoder.encode("0000"));
		test.setRoles(roles);

		info.setEmail(email);
		test.setInfo(identityRepository.save(info));

		return repository.save(test);
	}

	// Mock merchant user.
	public static Merchant createMockMerchant(MerchantRepository repository, AccountInfoRepository identityRepository,
			PasswordEncoder passwordEncoder, String username, String email) {
		Merchant test = new Merchant();
		AccountInfo info = new AccountInfo();

		test.setUsername(username);
		test.setInfo(identityRepository.save(info));

		info.setEmail(email);
		test.setPassword(passwordEncoder.encode("0000"));

		return repository.save(test);
	}

	// Mock personal user.
	public static Personal createMockPersonal(PersonalRepository repository, AccountInfoRepository identityRepository,
			PasswordEncoder passwordEncoder, String username, String email) {
		Personal test = new Personal();
		AccountInfo info = new AccountInfo();

		test.setUsername(username);
		test.setPassword(passwordEncoder.encode("0000"));

		info.setEmail(email);
		test.setInfo(identityRepository.save(info));

		return repository.save(test);
	}

	// Mock merchant donation.
	public static void createMockMerchantDonation(MerchantDonationRepository donationRepository, Merchant merchant,
			BigDecimal amount, PaymentStatus status, LocalDateTime paidAt) {
		MerchantDonation donation = new MerchantDonation();
		donation.setMerchant(merchant);
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);
		donation.setDonationType(DonationType.DIRECT);

		donationRepository.save(donation);
	}

	// Mock personal donation.
	public static void createMockPersonalDonation(PersonalDonationRepository donationRepository, Personal personal,
			BigDecimal amount, DonationType type, PaymentStatus status, LocalDateTime paidAt) {
		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);
		donation.setDonationType(type);
		donation.setTaxExempt(false);

		donationRepository.save(donation);
	}

	// Mock project donation.
	public static PersonalDonation createMockProjectDonation(PersonalDonationRepository donationRepository,
			Personal personal, Project project, BigDecimal amount, PaymentStatus status, LocalDateTime paidAt) {
		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setProject(project);
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);
		donation.setDonationType(DonationType.PROJECT);
		donation.setTaxExempt(false);

		return donationRepository.save(donation);
	}

	// Mock project.
	public static Project createMockProject(ProjectRepository repository, String name, BigDecimal targetAmount,
			ContentStatus status) {
		Project test = new Project();

		test.setName(name);
		test.setSlugUrl("slug-url");
		test.setTargetAmount(targetAmount);
		test.setLocation("location");
		test.setCategory(null);
		test.setTags(new HashSet<>());
		test.setSummary("summary");
		test.setContentHtml("content");
		test.setStatus(status);
		test.setImages(new ArrayList<>());
		test.setPaymentCollectionCode(UUID.randomUUID().toString());

		return repository.save(test);
	}

	// Mock news.
	public static void createMockNews(NewsRepository repository, String title, ContentStatus status) {
		News test = new News();

		test.setTitle(title);
		test.setSlugUrl("slug-url");
		test.setAuthor("author");
		test.setDate(LocalDate.now());
		test.setCategory(null);
		test.setTags(new HashSet<>());
		test.setSummary("summary");
		test.setContentHtml("content");
		test.setStatus(status);
		test.setImages(new ArrayList<>());

		repository.save(test);
	}

	// Mock campaign.
	public static void createMockCampaign(CampaignRepository repository, String name, ContentStatus status) {
		Campaign test = new Campaign();

		test.setName(name);
		test.setSlugUrl("slug-url");
		test.setDateStart(LocalDate.now());
		test.setDateEnd(LocalDate.now());
		test.setCategory(null);
		test.setTags(new HashSet<>());
		test.setSummary("summary");
		test.setContentHtml("content");
		test.setStatus(status);
		test.setImages(new ArrayList<>());

		repository.save(test);
	}

	// Mock profile.
	public static void createMockProfile(OrganizationRepository repository) {
		OrganizationProfile test = new OrganizationProfile();

		test.setName("Taqwa");
		test.setPhone("011-5432 6360");
		test.setEmail("info@waqaftaqwa.com");
		test.setAddressLine1("Tingkat 1, Anjung Niaga");
		test.setAddressLine2("Masjid At-Taqwa, Jalan Dato' Sulaiman");
		test.setAddressLine3("Taman Tun Dr Ismail");
		test.setPostcode((long) 60000);
		test.setCity("Kuala Lumpur");
		test.setCountry("Wilayah Persekutuan");
		test.setCountry("Malaysia");
		test.setContentHtml("content");
		test.setLogoKey(null);
		test.setHeroKey(null);

		repository.save(test);
	}

	// Mock merchant rakanqr.
	public static RakanQr createMockRakanQr(RakanQrRepository agentRepository, Merchant merchant, RakanQrType type,
			RakanQrStatus status) {
		RakanQr agent = new RakanQr();

		agent.setType(type);
		agent.setStatus(status);
		agent.setMerchant(merchant);

		return agentRepository.save(agent);
	}

	// Mock personal rakanqr.
	public static RakanQr createMockRakanQr(RakanQrRepository agentRepository, Personal personal, RakanQrType type,
			RakanQrStatus status) {
		RakanQr agent = new RakanQr();

		agent.setType(type);
		agent.setStatus(status);
		agent.setPersonal(personal);

		return agentRepository.save(agent);
	}

	// Mock rakanqr donation.
	public static RakanQrDonation createMockRakanQrDonation(RakanQrDonationRepository donationRepository, RakanQr agent,
			BigDecimal amount, PaymentStatus status, LocalDateTime paidAt) {
		RakanQrDonation donation = new RakanQrDonation();

		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setRakanQr(agent);
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);

		return donationRepository.save(donation);
	}

}
