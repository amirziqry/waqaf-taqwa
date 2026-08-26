package com.taqwa.gowaqaf.module.rakanqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
import com.taqwa.gowaqaf.mockuser.donator.WithMockPersonal;
import com.taqwa.gowaqaf.mockuser.vendor.WithMockMerchant;
import com.taqwa.gowaqaf.modules.agent.component.AgentStatus;
import com.taqwa.gowaqaf.modules.agent.component.AgentType;
import com.taqwa.gowaqaf.modules.agent.entity.RakanQr;
import com.taqwa.gowaqaf.modules.agent.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.donation.agent.dto.RakanQrDonationSum;
import com.taqwa.gowaqaf.modules.donation.agent.entity.RakanQrDonation;
import com.taqwa.gowaqaf.modules.donation.agent.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PaymentStatus;
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

	@SuppressWarnings("unused")
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final MerchantRepository merchantRepository;
	private final PersonalRepository personalRepository;
	private final RakanQrRepository agentRepository;
	private final RakanQrDonationRepository donationRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		createMockDonation(
				createMockAgent(createMockMerchant("merchant1", "merchant1@gmail.com"), null, AgentType.MERCHANT,
						AgentStatus.ACTIVE),
				new BigDecimal("200.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		createMockDonation(
				createMockAgent(createMockMerchant("merchant2", "merchant2@gmail.com"), null, AgentType.MERCHANT,
						AgentStatus.ACTIVE),
				new BigDecimal("150.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		createMockDonation(
				createMockAgent(null, createMockPersonal("personal1", "personal1@gmail.com"), AgentType.PERSONAL,
						AgentStatus.ACTIVE),
				new BigDecimal("250.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		createMockDonation(
				createMockAgent(null, createMockPersonal("personal2", "personal2@gmail.com"), AgentType.PERSONAL,
						AgentStatus.ACTIVE),
				new BigDecimal("50.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));

	}

	private Merchant createMockMerchant(String username, String email) {
		Merchant test = new Merchant();
		test.setUsername(username);
		test.setEmail(email);
		test.setPassword(passwordEncoder.encode("0000"));

		return merchantRepository.save(test);
	}

	private Personal createMockPersonal(String username, String email) {
		Personal test = new Personal();
		test.setUsername(username);
		test.setEmail(email);
		test.setPassword(passwordEncoder.encode("0000"));

		return personalRepository.save(test);
	}

	private RakanQr createMockAgent(Merchant merchant, Personal personal, AgentType type, AgentStatus status) {
		RakanQr agent = new RakanQr();

		agent.setType(type);
		agent.setStatus(status);

		if (type == AgentType.MERCHANT)
			agent.setMerchant(merchant);

		if (type == AgentType.PERSONAL)
			agent.setPersonal(personal);

		return agentRepository.save(agent);
	}

	private void createMockDonation(RakanQr agent, BigDecimal amount, PaymentStatus status, LocalDateTime paidAt) {
		RakanQrDonation donation = new RakanQrDonation();

		donation.setBillingCode(UUID.randomUUID().toString());
		donation.setRakanQr(agent);
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);

		donationRepository.save(donation);
	}

	@Test
	@WithMockMerchant(username = "merchantmock")
	void merchantAgentSummaryFlowTest() throws Exception {
		Merchant mock = merchantRepository.findByUsername("merchantmock").get();

		RakanQr agent = createMockAgent(mock, null, AgentType.MERCHANT, AgentStatus.ACTIVE);

		createMockDonation(agent, new BigDecimal("100.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		createMockDonation(agent, new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 10, 14, 30, 0));
		createMockDonation(agent, new BigDecimal("25.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 15, 18, 0, 0));
		createMockDonation(agent, new BigDecimal("100.00"), PaymentStatus.PENDING,
				LocalDateTime.of(2026, 8, 20, 12, 0, 0));

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

		RakanQr agent = createMockAgent(null, mock, AgentType.PERSONAL, AgentStatus.ACTIVE);

		createMockDonation(agent, new BigDecimal("100.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		createMockDonation(agent, new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 10, 14, 30, 0));
		createMockDonation(agent, new BigDecimal("25.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 15, 18, 0, 0));
		createMockDonation(agent, new BigDecimal("100.00"), PaymentStatus.PENDING,
				LocalDateTime.of(2026, 8, 20, 12, 0, 0));

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
