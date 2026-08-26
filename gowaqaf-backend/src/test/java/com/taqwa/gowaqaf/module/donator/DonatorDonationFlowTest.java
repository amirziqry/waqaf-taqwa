package com.taqwa.gowaqaf.module.donator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class DonatorDonationFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final PersonalDonationRepository personalDonationRepository;
	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;
	private Personal test;

	@BeforeEach
	void setup() {
		Personal test = new Personal();
		test.setUsername("donator_test");
		test.setPassword(passwordEncoder.encode("0000"));

		this.test = personalRepository.save(test);

		createMockDonation(this.test, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID);
		createMockDonation(this.test, UUID.randomUUID(), new BigDecimal("150.00"), PaymentStatus.PAID);
	}

	private void createMockDonation(Personal personal, UUID id, BigDecimal amount, PaymentStatus status) {
		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);

		personalDonationRepository.save(donation);
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void donatorDonationSummaryFlowTest() throws Exception {
		Personal mock = personalRepository.findByUsername("donator_mock").get();

		createMockDonation(mock, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID);
		createMockDonation(mock, UUID.randomUUID(), new BigDecimal("50.00"), PaymentStatus.PAID);
		createMockDonation(mock, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PENDING);

		MvcResult result = mockMvc.perform(get("/api/donator/donation/sum")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		PersonalDonationSum sum = objectMapper.readValue(response, PersonalDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("150.00"), sum.total());
	}

}
