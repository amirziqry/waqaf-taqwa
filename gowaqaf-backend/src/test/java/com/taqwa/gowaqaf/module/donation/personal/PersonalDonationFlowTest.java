package com.taqwa.gowaqaf.module.donation.personal;

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
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.donation.enums.DonationType;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class PersonalDonationFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final PersonalDonationRepository personalDonationRepository;
	private final AccountInfoRepository identityRepository;
	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;
	private Personal test;

	@BeforeEach
	void setup() {
		this.test = CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"donator_test", "test@gmail.com");

		CommonClass.createMockPersonalDonation(personalDonationRepository, this.test, new BigDecimal("100.00"),
				DonationType.DIRECT, PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockPersonalDonation(personalDonationRepository, this.test, new BigDecimal("150.00"),
				DonationType.DIRECT, PaymentStatus.PAID, LocalDateTime.now());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void donatorDonationSummaryFlowTest() throws Exception {
		Personal mock = personalRepository.findByUsername("donator_mock").get();

		CommonClass.createMockPersonalDonation(personalDonationRepository, mock, new BigDecimal("100.00"),
				DonationType.DIRECT, PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockPersonalDonation(personalDonationRepository, mock, new BigDecimal("50.00"),
				DonationType.DIRECT, PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockPersonalDonation(personalDonationRepository, mock, new BigDecimal("100.00"),
				DonationType.DIRECT, PaymentStatus.PENDING, LocalDateTime.now());

		MvcResult result = mockMvc.perform(get("/api/personal/donation/sum")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		PersonalDonationSum sum = objectMapper.readValue(response, PersonalDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("150.00"), sum.total());
	}

}
