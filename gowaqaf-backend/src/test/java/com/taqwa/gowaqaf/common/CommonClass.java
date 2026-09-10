package com.taqwa.gowaqaf.common;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.merchant.entity.MerchantDonation;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.entity.ProjectDonation;
import com.taqwa.gowaqaf.modules.donation.project.repository.ProjectDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrAccount;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Donation;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.organization.content.campaign.entity.Campaign;
import com.taqwa.gowaqaf.modules.organization.content.campaign.repository.CampaignRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.category.entity.ContentCategory;
import com.taqwa.gowaqaf.modules.organization.content.component.category.repository.ContentCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.content.component.enums.ContentType;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.entity.ContentTag;
import com.taqwa.gowaqaf.modules.organization.content.component.tag.repository.ContentTagRepository;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.news.entity.News;
import com.taqwa.gowaqaf.modules.organization.content.news.repository.NewsRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.organization.profile.entity.OrganizationProfile;
import com.taqwa.gowaqaf.modules.organization.profile.repository.OrganizationRepository;
import com.taqwa.gowaqaf.modules.user.account.entity.AccountInfo;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

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
		test.setPassword(passwordEncoder.encode("0000"));

		info.setAccountHolderName("John Doe");
		info.setPhone("60123456789");
		info.setEmail(email);
		test.setInfo(identityRepository.save(info));

		return repository.save(test);
	}

	// Mock personal user.
	public static Personal createMockPersonal(PersonalRepository repository, AccountInfoRepository identityRepository,
			PasswordEncoder passwordEncoder, String username, String email) {
		Personal test = new Personal();
		AccountInfo info = new AccountInfo();

		test.setUsername(username);
		test.setPassword(passwordEncoder.encode("0000"));

		info.setAccountHolderName("Jane Doe");
		info.setPhone("60123456789");
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
	public static void createMockPersonalDonation(DonationRepository repository,
			PersonalDonationRepository donationRepository, Personal personal, BigDecimal amount, DonationType type,
			PaymentStatus status, LocalDateTime paidAt) {
		Donation donation = new Donation();
		PersonalDonation personalDonation = new PersonalDonation();

		// Parent donation
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);
		donation.setStatus(status);
		donation.setDonationType(type);
		donation.setWebhookToken(null);

		donation = repository.save(donation);

		// Personal donation
		personalDonation.setId(donation.getId());
		personalDonation.setDonation(donation);
		personalDonation.setPersonal(personal);
		personalDonation.setTaxExempt(false);

		donationRepository.save(personalDonation);
	}

	// Mock project donation.
	public static ProjectDonation createMockProjectDonation(DonationRepository repository,
			ProjectDonationRepository donationRepository, Personal personal, Project project, BigDecimal amount,
			PaymentStatus status, LocalDateTime paidAt) {
		Donation donation = new Donation();
		ProjectDonation projectDonation = new ProjectDonation();

		// Parent donation
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setPaidAt(paidAt);
		donation.setStatus(status);
		donation.setDonationType(DonationType.PROJECT);
		donation.setWebhookToken(null);

		donation = repository.save(donation);

		// Project donation
		projectDonation.setId(donation.getId());
		projectDonation.setDonation(donation);
		projectDonation.setProject(project);
		projectDonation.setPersonal(personal);
		projectDonation.setMerchant(null);
		projectDonation.setTaxExempt(false);

		return donationRepository.save(projectDonation);
	}

	// Mock project donation.
	public static ProjectDonation createMockProjectDonation(ProjectDonationRepository donationRepository,
			Merchant merchant, Project project, BigDecimal amount, PaymentStatus status, LocalDateTime paidAt) {
		Donation donation = new Donation();
		ProjectDonation projectDonation = new ProjectDonation();

		// Parent donation
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setPaidAt(paidAt);
		donation.setStatus(status);
		donation.setDonationType(DonationType.PROJECT);
		donation.setWebhookToken(null);

		// Project donation
		projectDonation.setDonation(donation);
		projectDonation.setProject(project);
		projectDonation.setMerchant(merchant);
		projectDonation.setPersonal(null);
		projectDonation.setTaxExempt(false);

		return donationRepository.save(projectDonation);
	}

	// Mock category.
	public static ContentCategory createMockCategory(ContentCategoryRepository repository, String name,
			ContentType type) {
		ContentCategory category = new ContentCategory();
		category.setName(name);
		category.setType(type);

		return repository.save(category);
	}

	// Mock tag.
	public static ContentTag createMockTag(ContentTagRepository repository, String name, ContentType type) {
		ContentTag tag = new ContentTag();
		tag.setName(name);
		tag.setType(type);

		return repository.save(tag);
	}

	// Mock project.
	public static Project createMockProject(ProjectRepository repository, String name, BigDecimal targetAmount,
			ContentStatus status) {
		Project test = new Project();

		test.setName(name);
		test.setSlugUrl("slug-url");
		test.setCollectedAmount(new BigDecimal("0.00"));
		test.setTargetAmount(targetAmount);
		test.setLocation("location");
		test.setCategory(null);
		test.setTags(new HashSet<>());
		test.setSummary("summary");
		test.setContentHtml("content");
		test.setStatus(status);
		test.setImages(new ArrayList<>());

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

		agent.setCode(generateRakanQrCode());
		agent.setAccount(RakanQrAccount.MERCHANT);
		agent.setType(type);
		agent.setStatus(status);
		agent.setMerchant(merchant);
		agent.setCollectedAmount(new BigDecimal("0.00"));
		if (agent.getType() == RakanQrType.AMBASSADOR)
			agent.setCommission(new BigDecimal("0.00"));

		return agentRepository.save(agent);
	}

	// Mock personal rakanqr.
	public static RakanQr createMockRakanQr(RakanQrRepository agentRepository, Personal personal, RakanQrType type,
			RakanQrStatus status) {
		RakanQr agent = new RakanQr();

		agent.setCode(generateRakanQrCode());
		agent.setAccount(RakanQrAccount.PERSONAL);
		agent.setType(type);
		agent.setStatus(status);
		agent.setPersonal(personal);
		agent.setCollectedAmount(new BigDecimal("0.00"));
		if (agent.getType() == RakanQrType.AMBASSADOR)
			agent.setCommission(new BigDecimal("0.00"));

		return agentRepository.save(agent);
	}

	private static String generateRakanQrCode() {
		return "RQR" + ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
	}

	// Mock rakanqr donation.
	public static RakanQrDonation createMockRakanQrDonation(DonationRepository repository,
			RakanQrDonationRepository donationRepository, RakanQr agent, BigDecimal amount, PaymentStatus status,
			LocalDateTime paidAt) {
		Donation donation = new Donation();
		RakanQrDonation rakanQrDonation = new RakanQrDonation();

		// Parent donation
		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setAmount(amount);
		donation.setPaidAt(paidAt);
		donation.setStatus(status);
		donation.setDonationType(DonationType.RAKANQR);
		donation.setWebhookToken(null);

		donation = repository.save(donation);

		// Project donation
		rakanQrDonation.setId(donation.getId());
		rakanQrDonation.setDonation(donation);
		rakanQrDonation.setRakanQr(agent);

		return donationRepository.save(rakanQrDonation);
	}

	// Object Storage

	// Test save file
	public static void testSaveFileToStorage(HttpClient httpClient, String url, byte[] fileBytes) throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "image/jpeg")
				.PUT(HttpRequest.BodyPublishers.ofByteArray(fileBytes)).build();

		HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, response.statusCode());
	}

	// Test get images (list)
	public static void testGetImagesFromStorage(HttpClient httpClient, List<String> urls) throws Exception {
		urls.forEach(url -> {
			try {
				testGetImageFromStorage(httpClient, url);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	// Test get image
	public static void testGetImageFromStorage(HttpClient httpClient, String url) throws Exception {
		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<byte[]> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(200, getResponse.statusCode());
	}

	// Test delete file
	public static void deleteFileFromStorage(String fileKey, S3Client s3Client, String bucket) {
		s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(fileKey).build());
	}

	// Test get deleted file
	public static void testDeletedImageFromStorage(HttpClient httpClient, String url) throws Exception {
		HttpRequest getRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

		HttpResponse<byte[]> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());

		Assertions.assertEquals(404, getResponse.statusCode());
	}

}
