package com.taqwa.gowaqaf.module.dashboard;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.organization.content.campaign.repository.CampaignRepository;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.news.repository.NewsRepository;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.organization.profile.repository.OrganizationRepository;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class AdminDashboardFlowTest {

	@SuppressWarnings("unused")
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	private final PersonalRepository personalRepository;
	private final MerchantRepository merchantRepository;
	private final RakanQrRepository agentRepository;
	private final AccountInfoRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	private final PersonalDonationRepository personalDonationRepository;
	private final MerchantDonationRepository merchantDonationRepository;
	private final RakanQrDonationRepository agentDonationRepository;

	private final ProjectRepository projectRepository;
	private final NewsRepository newsRepository;
	private final CampaignRepository campaignRepository;
	private final OrganizationRepository profileRepository;

	@BeforeEach
	void setup() {
		CommonClass.createMockProfile(profileRepository);
		CommonClass.createMockProject(projectRepository, "project1", new BigDecimal("10000"), ContentStatus.PUBLISHED);
		CommonClass.createMockProject(projectRepository, "project2", new BigDecimal("10000"), ContentStatus.PUBLISHED);
		CommonClass.createMockProject(projectRepository, "project3", new BigDecimal("10000"), ContentStatus.PUBLISHED);
		CommonClass.createMockNews(newsRepository, "news1", ContentStatus.PUBLISHED);
		CommonClass.createMockNews(newsRepository, "news2", ContentStatus.PUBLISHED);
		CommonClass.createMockCampaign(campaignRepository, "campaign1", ContentStatus.PUBLISHED);

		Personal p1 = CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"donator1", "test@gmail.com");
		Personal p2 = CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"donator2", "test@gmail.com");

		// Personal - DIRECT
		CommonClass.createMockPersonalDonation(personalDonationRepository, p1, new BigDecimal("100.00"),
				DonationType.DIRECT, PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockPersonalDonation(personalDonationRepository, p2, new BigDecimal("50.00"),
				DonationType.DIRECT, PaymentStatus.PAID, LocalDateTime.of(2026, 8, 10, 14, 30, 0));

		// Personal - RECURRING
		CommonClass.createMockPersonalDonation(personalDonationRepository, p1, new BigDecimal("75.00"),
				DonationType.RECURRING, PaymentStatus.PAID, LocalDateTime.of(2026, 8, 5, 12, 0, 0));
		CommonClass.createMockPersonalDonation(personalDonationRepository, p2, new BigDecimal("25.00"),
				DonationType.RECURRING, PaymentStatus.PAID, LocalDateTime.of(2026, 8, 15, 18, 0, 0));

		// Personal - PROJECT
		CommonClass.createMockPersonalDonation(personalDonationRepository, p1, new BigDecimal("200.00"),
				DonationType.PROJECT, PaymentStatus.PAID, LocalDateTime.of(2026, 8, 12, 15, 0, 0));
		CommonClass.createMockPersonalDonation(personalDonationRepository, p2, new BigDecimal("100.00"),
				DonationType.PROJECT, PaymentStatus.PAID, LocalDateTime.of(2026, 8, 20, 16, 0, 0));

		// Personal - should NOT be included
		CommonClass.createMockPersonalDonation(personalDonationRepository, p1, new BigDecimal("100.00"),
				DonationType.DIRECT, PaymentStatus.UNPAID, LocalDateTime.of(2026, 8, 25, 12, 0, 0));

		Merchant m1 = CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder, "vendor1",
				"test@gmail.com");
		Merchant m2 = CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder, "vendor2",
				"test@gmail.com");

		// Merchant - PAID
		CommonClass.createMockMerchantDonation(merchantDonationRepository, m1, new BigDecimal("200.00"),
				PaymentStatus.PAID, LocalDateTime.of(2026, 8, 5, 10, 0, 0));
		CommonClass.createMockMerchantDonation(merchantDonationRepository, m1, new BigDecimal("50.00"),
				PaymentStatus.PAID, LocalDateTime.of(2026, 8, 12, 15, 0, 0));
		CommonClass.createMockMerchantDonation(merchantDonationRepository, m2, new BigDecimal("25.00"),
				PaymentStatus.PAID, LocalDateTime.of(2026, 8, 18, 20, 0, 0));

		// Merchant - should NOT be included
		CommonClass.createMockMerchantDonation(merchantDonationRepository, m1, new BigDecimal("100.00"),
				PaymentStatus.UNPAID, LocalDateTime.of(2026, 8, 25, 12, 0, 0));

		// Rakan QR

		RakanQr r1 = CommonClass.createMockRakanQr(agentRepository, p1, RakanQrType.PERSONAL, RakanQrStatus.ACTIVE);
		RakanQr r2 = CommonClass.createMockRakanQr(agentRepository, m1, RakanQrType.MERCHANT, RakanQrStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(agentDonationRepository, r1, new BigDecimal("150.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 3, 11, 0, 0));
		CommonClass.createMockRakanQrDonation(agentDonationRepository, r2, new BigDecimal("75.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 14, 13, 30, 0));
		CommonClass.createMockRakanQrDonation(agentDonationRepository, r1, new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 22, 17, 0, 0));

		// Rakan QR - should NOT be included
		CommonClass.createMockRakanQrDonation(agentDonationRepository, r2, new BigDecimal("100.00"),
				PaymentStatus.UNPAID, LocalDateTime.of(2026, 8, 25, 19, 0, 0));
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void adminDashboardFlowTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/admin/dashboard/get")).andExpect(status().isOk())
				.andExpect(status().isOk()).andExpect(jsonPath("$.collectionSum").exists())
				.andExpect(jsonPath("$.rakanQrSummary").isArray())
				.andExpect(jsonPath("$.rakanQrSummary.length()").value(2)).andExpect(jsonPath("$.projects").isArray())
				.andExpect(jsonPath("$.projects.length()").value(3)).andExpect(jsonPath("$.news").isArray())
				.andExpect(jsonPath("$.news.length()").value(2)).andExpect(jsonPath("$.campaigns").isArray())
				.andExpect(jsonPath("$.campaigns.length()").value(1))
				.andExpect(jsonPath("$.organizationProfile").exists()).andReturn();

		System.out.println(result.getResponse().getContentAsString());
	}

}
