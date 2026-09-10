package com.taqwa.gowaqaf.module.dashboard;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.common.CommonEndpoints;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.repository.ProjectDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.organization.collection.repository.DonationRepository;
import com.taqwa.gowaqaf.modules.organization.content.campaign.repository.CampaignRepository;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.news.repository.NewsRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.organization.profile.repository.OrganizationRepository;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class PersonalDashboardFlowTest {

	@SuppressWarnings("unused")
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	private final DonationRepository donationRepository;
	private final PersonalRepository personalRepository;
	private final AccountInfoRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	private final RakanQrRepository rakanQrRepository;
	private final RakanQrDonationRepository rakanQrDonationRepository;

	private final PersonalDonationRepository personalDonationRepository;
	private final ProjectDonationRepository projectDonationRepository;

	private final ProjectRepository projectRepository;
	private final NewsRepository newsRepository;
	private final CampaignRepository campaignRepository;
	private final OrganizationRepository profileRepository;

	@BeforeEach
	void setup() {
		CommonClass.createMockProfile(profileRepository);
		Project c1 = CommonClass.createMockProject(projectRepository, "project1", new BigDecimal("10000"),
				ContentStatus.PUBLISHED);
		CommonClass.createMockProject(projectRepository, "project2", new BigDecimal("10000"), ContentStatus.PUBLISHED);
		CommonClass.createMockProject(projectRepository, "project3", new BigDecimal("10000"), ContentStatus.PUBLISHED);
		CommonClass.createMockNews(newsRepository, "news1", ContentStatus.PUBLISHED);
		CommonClass.createMockNews(newsRepository, "news2", ContentStatus.PUBLISHED);
		CommonClass.createMockCampaign(campaignRepository, "campaign1", ContentStatus.PUBLISHED);

		Personal p1 = CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"personal1", "test@gmail.com");

		// Personal - DIRECT
		CommonClass.createMockPersonalDonation(donationRepository, personalDonationRepository, p1,
				new BigDecimal("100.00"), DonationType.DIRECT, PaymentStatus.PAID,
				LocalDateTime.of(2026, 9, 1, 10, 0, 0));
		CommonClass.createMockPersonalDonation(donationRepository, personalDonationRepository, p1,
				new BigDecimal("50.00"), DonationType.DIRECT, PaymentStatus.PAID,
				LocalDateTime.of(2026, 9, 3, 14, 30, 0));
		CommonClass.createMockPersonalDonation(donationRepository, personalDonationRepository, p1,
				new BigDecimal("100.00"), DonationType.DIRECT, PaymentStatus.PAID,
				LocalDateTime.of(2026, 9, 5, 10, 0, 0));
		CommonClass.createMockPersonalDonation(donationRepository, personalDonationRepository, p1,
				new BigDecimal("50.00"), DonationType.DIRECT, PaymentStatus.PAID,
				LocalDateTime.of(2026, 9, 7, 14, 30, 0));

		// Personal - PROJECT
		CommonClass.createMockProjectDonation(donationRepository, projectDonationRepository, p1, c1,
				new BigDecimal("200.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 9, 2, 15, 0, 0));
		CommonClass.createMockProjectDonation(donationRepository, projectDonationRepository, p1, c1,
				new BigDecimal("100.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 9, 4, 16, 0, 0));

		RakanQr r1 = CommonClass.createMockRakanQr(rakanQrRepository, p1, RakanQrType.STANDARD, RakanQrStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(donationRepository, rakanQrDonationRepository, r1,
				new BigDecimal("10.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 9, 2, 16, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, rakanQrDonationRepository, r1, new BigDecimal("5.00"),
				PaymentStatus.PAID, LocalDateTime.of(2026, 9, 3, 16, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, rakanQrDonationRepository, r1,
				new BigDecimal("15.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 9, 4, 16, 0, 0));

	}

	@Test
	void personalDashboardTest() throws Exception {

		String requestBody = """
				{
				  "username": "personal1",
				  "password": "0000"
				}
				""";

		MvcResult response = mockMvc.perform(
				post(CommonEndpoints.personalLogin).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

		Cookie accessToken = response.getResponse().getCookie("accessToken");

		assertNotNull(accessToken);

		mockMvc.perform(get(CommonEndpoints.personalDashboard).cookie(accessToken)).andExpect(status().isOk())

				// =========================
				// Donation total
				// =========================
				.andExpect(jsonPath("$.contributions").exists())
				.andExpect(jsonPath("$.contributions.total").value(600.0))

				// =========================
				// User information
				// =========================
				.andExpect(jsonPath("$.accountInfo").exists())

				// =========================
				// Organization profile
				// =========================
				.andExpect(jsonPath("$.orgAbout").exists())

				// =========================
				// Projects
				// =========================
				.andExpect(jsonPath("$.projects").exists()).andExpect(jsonPath("$.projects").isArray())
				.andExpect(jsonPath("$.projects", Matchers.hasSize(3)))

				// =========================
				// News
				// =========================
				.andExpect(jsonPath("$.news").exists()).andExpect(jsonPath("$.news").isArray())
				.andExpect(jsonPath("$.news", Matchers.hasSize(2)))

				// =========================
				// Campaigns
				// =========================
				.andExpect(jsonPath("$.campaigns").exists()).andExpect(jsonPath("$.campaigns").isArray())
				.andExpect(jsonPath("$.campaigns", Matchers.hasSize(1)))

				// =========================
				// Donations
				// =========================
				.andExpect(jsonPath("$.donations").exists()).andExpect(jsonPath("$.donations").isArray())
				.andExpect(jsonPath("$.donations", Matchers.hasSize(4)))

				.andDo(print());

		mockMvc.perform(get(CommonEndpoints.rakanQrDashboard).cookie(accessToken)).andExpect(status().isOk())

				// =========================
				// Info
				// =========================
				.andExpect(jsonPath("$.info").exists())

				// =========================
				// Collected amount
				// =========================
				.andExpect(jsonPath("$.collectedAmount").exists())
				.andExpect(jsonPath("$.collectedAmount.total").value(30.0))

				// =========================
				// Donations
				// =========================
				.andExpect(jsonPath("$.donations").exists()).andExpect(jsonPath("$.donations").isArray())
				.andExpect(jsonPath("$.donations", Matchers.hasSize(3)))

				.andDo(print());
	}

}
