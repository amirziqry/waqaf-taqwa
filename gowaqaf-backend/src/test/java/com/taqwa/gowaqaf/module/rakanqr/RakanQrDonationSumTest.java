package com.taqwa.gowaqaf.module.rakanqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hamcrest.Matchers;
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
import com.taqwa.gowaqaf.mockuser.donator.WithMockPersonal;
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.vendor.WithMockMerchant;
import com.taqwa.gowaqaf.modules.agent.component.AgentStatus;
import com.taqwa.gowaqaf.modules.agent.component.AgentType;
import com.taqwa.gowaqaf.modules.agent.entity.RakanQr;
import com.taqwa.gowaqaf.modules.agent.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationSum;
import com.taqwa.gowaqaf.modules.donation.agent.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountIdentityRepository;
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
public class RakanQrDonationSumTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final MerchantRepository merchantRepository;
	private final PersonalRepository personalRepository;
	private final AccountIdentityRepository identityRepository;
	private final RakanQrRepository agentRepository;
	private final RakanQrDonationRepository donationRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Merchant m1 = CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"merchant1", "merchant1@gmail.com");
		Merchant m2 = CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"merchant2", "merchant2@gmail.com");
		Personal p1 = CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"personal1", "personal1@gmail.com");
		Personal p2 = CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"personal2", "personal2@gmail.com");

		RakanQr mr1 = CommonClass.createMockRakanQr(agentRepository, m1, AgentType.MERCHANT, AgentStatus.ACTIVE);
		RakanQr mr2 = CommonClass.createMockRakanQr(agentRepository, m2, AgentType.MERCHANT, AgentStatus.ACTIVE);
		RakanQr pr1 = CommonClass.createMockRakanQr(agentRepository, p1, AgentType.PERSONAL, AgentStatus.ACTIVE);
		RakanQr pr2 = CommonClass.createMockRakanQr(agentRepository, p2, AgentType.PERSONAL, AgentStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(donationRepository, mr1, new BigDecimal("200.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, mr2, new BigDecimal("150.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, pr1, new BigDecimal("250.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, pr2, new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
	}

	@Test
	@WithMockAdmin(username = "supermember", roles = { "ADMIN" })
	void AdminGetSummaryFlowTest() throws Exception {
		mockMvc.perform(get("/api/rakan-qr-agent/get/sum/all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[*].totalCollected", Matchers.containsInAnyOrder(200.00, 150.00, 250.00, 50.00)));
	}

	@Test
	@WithMockMerchant(username = "merchantmock")
	void merchantAgentSummaryFlowTest() throws Exception {
		Merchant mock = merchantRepository.findByUsername("merchantmock").get();

		RakanQr agent = CommonClass.createMockRakanQr(agentRepository, mock, AgentType.MERCHANT, AgentStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("100.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 10, 14, 30, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("25.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 15, 18, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("100.00"),
				PaymentStatus.PENDING, LocalDateTime.of(2026, 8, 20, 12, 0, 0));

		MvcResult result = mockMvc.perform(get("/api/rakan-qr-agent/donation/sum")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		RakanQrDonationSum sum = objectMapper.readValue(response, RakanQrDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("175.00"), sum.total());

		result = mockMvc.perform(
				get("/api/rakan-qr-agent/donation/sum").param("startDate", "05-08-2026").param("endDate", "15-08-2026"))
				.andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		sum = objectMapper.readValue(response, RakanQrDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("75.00"), sum.total());
	}

	@Test
	@WithMockPersonal(username = "personalmock")
	void personalAgentSummaryFlowTest() throws Exception {
		Personal mock = personalRepository.findByUsername("personalmock").get();

		RakanQr agent = CommonClass.createMockRakanQr(agentRepository, mock, AgentType.PERSONAL, AgentStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("100.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 10, 14, 30, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("25.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 15, 18, 0, 0));
		CommonClass.createMockRakanQrDonation(donationRepository, agent, new BigDecimal("100.00"),
				PaymentStatus.PENDING, LocalDateTime.of(2026, 8, 20, 12, 0, 0));

		MvcResult result = mockMvc.perform(get("/api/rakan-qr-agent/donation/sum")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		RakanQrDonationSum sum = objectMapper.readValue(response, RakanQrDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("175.00"), sum.total());

		result = mockMvc.perform(
				get("/api/rakan-qr-agent/donation/sum").param("startDate", "05-08-2026").param("endDate", "15-08-2026"))
				.andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		sum = objectMapper.readValue(response, RakanQrDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("75.00"), sum.total());
	}

}
