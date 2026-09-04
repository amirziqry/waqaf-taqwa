package com.taqwa.gowaqaf.module.organization.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSum;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class OrganizationCollectionSumFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	private final AccountInfoRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	private final PersonalRepository personalRepository;
	private final MerchantRepository merchantRepository;
	private final RakanQrRepository agentRepository;

	private final PersonalDonationRepository personalDonationRepository;
	private final MerchantDonationRepository merchantDonationRepository;
	private final RakanQrDonationRepository agentDonationRepository;

	@BeforeEach
	void setup() {
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
		RakanQr r2 = CommonClass.createMockRakanQr(agentRepository, m1, RakanQrType.PERSONAL, RakanQrStatus.ACTIVE);

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
	void orgCollectionSumTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/organization/collection/sum")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		OrganizationCollectionSum sum = objectMapper.readValue(response, OrganizationCollectionSum.class);

		assertNotNull(sum);

		assertEquals(new BigDecimal("150.00"), sum.personalDirectSum());
		assertEquals(new BigDecimal("100.00"), sum.personalRecurringSum());
		assertEquals(new BigDecimal("300.00"), sum.projectSum());
		assertEquals(new BigDecimal("275.00"), sum.merchantDirectSum());
		assertEquals(new BigDecimal("275.00"), sum.rakanQrSum());
		assertEquals(new BigDecimal("1100.00"), sum.total());
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void orgCollectionSumWithDateRangeTest() throws Exception {
		MvcResult result = mockMvc.perform(
				get("/api/organization/collection/sum").param("startDate", "05-08-2026").param("endDate", "15-08-2026"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		OrganizationCollectionSum sum = objectMapper.readValue(response, OrganizationCollectionSum.class);

		assertNotNull(sum);

		assertEquals(new BigDecimal("50.00"), sum.personalDirectSum());
		assertEquals(new BigDecimal("100.00"), sum.personalRecurringSum());
		assertEquals(new BigDecimal("200.00"), sum.projectSum());
		assertEquals(new BigDecimal("250.00"), sum.merchantDirectSum());
		assertEquals(new BigDecimal("75.00"), sum.rakanQrSum());
		assertEquals(new BigDecimal("675.00"), sum.total());
	}

}
