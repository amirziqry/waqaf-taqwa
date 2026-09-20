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
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.organization.about.entity.OrganizationAbout;
import com.taqwa.gowaqaf.modules.organization.about.repository.OrganizationAboutRepository;
import com.taqwa.gowaqaf.modules.organization.collection.entity.Transaction;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
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
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

public class CommonClass {

	// Mock admin user.
	public static Account createMockAdmin(AccountRepository repository, PasswordEncoder passwordEncoder,
			String username, String email, Role role) {
		Account test = new Account();

		test.setUsername(username);
		test.setPassword(passwordEncoder.encode("0000"));

		test.setAccountHolderName("Jane Doe");
		test.setPhone("6012" + new Random().nextInt(10000000));
		test.setEmail(email);
		test.setRoles(Set.of(role));

		return repository.save(test);
	}

	// Mock personal user.
	public static Account createMockPersonal(AccountRepository repository, PasswordEncoder passwordEncoder,
			String username, String email) {
		Account test = new Account();

		test.setUsername(username);
		test.setPassword(passwordEncoder.encode("0000"));

		test.setAccountHolderName("Jane Doe");
		test.setPhone("6012" + new Random().nextInt(10000000));
		test.setEmail(email);
		test.setRoles(Set.of(Role.USER));

		return repository.save(test);
	}

	// Mock personal donation.
	public static void createMockPersonalDonation(TransactionRepository repository,
			PersonalDonationRepository donationRepository, Account account, BigDecimal amount, DonationType type,
			PaymentStatus status, LocalDateTime paidAt) {
		Transaction transaction = new Transaction();
		PersonalDonation personalDonation = new PersonalDonation();

		// Parent donation
		transaction.setBillingCode(UUID.randomUUID().toString());
		transaction.setAmount(amount);
		transaction.setStatus(status);
		transaction.setPaidAt(paidAt);
		transaction.setStatus(status);
		transaction.setDonationType(type);
		transaction.setWebhookToken(null);

		transaction = repository.save(transaction);

		// Personal donation
		personalDonation.setId(transaction.getId());
		personalDonation.setTransaction(transaction);
		personalDonation.setAccount(account);

		donationRepository.save(personalDonation);
	}

	// Mock project donation.
	public static PersonalDonation createMockProjectDonation(TransactionRepository repository,
			PersonalDonationRepository donationRepository, Account account, Project project, BigDecimal amount,
			PaymentStatus status, LocalDateTime paidAt) {
		Transaction transaction = new Transaction();
		PersonalDonation projectDonation = new PersonalDonation();

		// Parent donation
		transaction.setBillingCode(UUID.randomUUID().toString());
		transaction.setAmount(amount);
		transaction.setPaidAt(paidAt);
		transaction.setStatus(status);
		transaction.setDonationType(DonationType.PROJECT);
		transaction.setWebhookToken(null);

		transaction = repository.save(transaction);

		// Project donation
		projectDonation.setId(transaction.getId());
		projectDonation.setTransaction(transaction);
		projectDonation.setProject(project);
		projectDonation.setAccount(account);

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
	public static void createMockProfile(OrganizationAboutRepository repository) {
		OrganizationAbout test = new OrganizationAbout();

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

	// Mock personal rakanqr.
	public static RakanQr createMockRakanQr(RakanQrRepository agentRepository, Account account, RakanQrType type,
			RakanQrStatus status) {
		RakanQr agent = new RakanQr();

		agent.setCode(generateRakanQrCode());
		agent.setAccount(account);
		agent.setType(type);
		agent.setStatus(status);
		agent.setCollectedAmount(new BigDecimal("0.00"));
		if (agent.getType() == RakanQrType.DUTA)
			agent.setCommission(new BigDecimal("0.00"));

		return agentRepository.save(agent);
	}

	private static String generateRakanQrCode() {
		return "RQR" + ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
	}

	// Mock rakanqr donation.
	public static RakanQrDonation createMockRakanQrDonation(TransactionRepository repository,
			RakanQrDonationRepository donationRepository, RakanQr agent, BigDecimal amount, PaymentStatus status,
			LocalDateTime paidAt) {
		Transaction transaction = new Transaction();
		RakanQrDonation rakanQrDonation = new RakanQrDonation();

		// Parent donation
		transaction.setBillingCode(UUID.randomUUID().toString());
		transaction.setAmount(amount);
		transaction.setPaidAt(paidAt);
		transaction.setStatus(status);
		transaction.setDonationType(DonationType.RAKANQR);
		transaction.setWebhookToken(null);

		transaction = repository.save(transaction);

		// Project donation
		rakanQrDonation.setId(transaction.getId());
		rakanQrDonation.setTransaction(transaction);
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
