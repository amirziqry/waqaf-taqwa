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
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.rakanqr.dto.RakanQrCollection;
import com.taqwa.gowaqaf.modules.donation.rakanqr.repository.RakanQrDonationRepository;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.organization.collection.repository.TransactionRepository;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;

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
	private final AccountRepository accountRepository;
	private final RakanQrRepository agentRepository;
	private final TransactionRepository transactionRepository;
	private final RakanQrDonationRepository rakanQrDonationRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {

		Account p1 = CommonClass.createMockPersonal(accountRepository, passwordEncoder, "personal1",
				"personal1@gmail.com");
		Account p2 = CommonClass.createMockPersonal(accountRepository, passwordEncoder, "personal2",
				"personal2@gmail.com");

		RakanQr mr1 = CommonClass.createMockRakanQr(agentRepository, p1, RakanQrType.AHLI, RakanQrStatus.ACTIVE);
		RakanQr mr2 = CommonClass.createMockRakanQr(agentRepository, p2, RakanQrType.AHLI, RakanQrStatus.ACTIVE);
		RakanQr pr1 = CommonClass.createMockRakanQr(agentRepository, p1, RakanQrType.AHLI, RakanQrStatus.ACTIVE);
		RakanQr pr2 = CommonClass.createMockRakanQr(agentRepository, p2, RakanQrType.AHLI, RakanQrStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, mr1,
				new BigDecimal("200.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, mr2,
				new BigDecimal("150.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, pr1,
				new BigDecimal("250.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, pr2,
				new BigDecimal("50.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
	}

	@Test
	@WithMockAdmin(username = "supermember", roles = { "ADMIN" })
	void AdminGetSummaryFlowTest() throws Exception {
		mockMvc.perform(get("/api/rakan-qr-agent/get/sum/all")).andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(4))
				.andExpect(jsonPath("$[*].totalCollected", Matchers.containsInAnyOrder(200.00, 150.00, 250.00, 50.00)));
	}

	@Test
	@WithMockPersonal(username = "personalmock")
	void personalAgentSummaryFlowTest() throws Exception {
		Account mock = accountRepository.findByUsername("personalmock").get();

		RakanQr agent = CommonClass.createMockRakanQr(agentRepository, mock, RakanQrType.AHLI, RakanQrStatus.ACTIVE);

		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, agent,
				new BigDecimal("100.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, agent,
				new BigDecimal("50.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 10, 14, 30, 0));
		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, agent,
				new BigDecimal("25.00"), PaymentStatus.PAID, LocalDateTime.of(2026, 8, 15, 18, 0, 0));
		CommonClass.createMockRakanQrDonation(transactionRepository, rakanQrDonationRepository, agent,
				new BigDecimal("100.00"), PaymentStatus.UNPAID, LocalDateTime.of(2026, 8, 20, 12, 0, 0));

		MvcResult result = mockMvc.perform(get("/api/rakan-qr-agent/donation/sum")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		RakanQrCollection sum = objectMapper.readValue(response, RakanQrCollection.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("175.00"), sum.total());

		result = mockMvc.perform(
				get("/api/rakan-qr-agent/donation/sum").param("startDate", "05-08-2026").param("endDate", "15-08-2026"))
				.andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		sum = objectMapper.readValue(response, RakanQrCollection.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("75.00"), sum.total());
	}

}
