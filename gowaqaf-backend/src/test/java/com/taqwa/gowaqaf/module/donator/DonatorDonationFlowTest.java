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
import com.taqwa.gowaqaf.mockuser.donator.WithMockDonator;
import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationSum;
import com.taqwa.gowaqaf.modules.donation.donator.entity.DonatorDonation;
import com.taqwa.gowaqaf.modules.donation.donator.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.donator.repository.DonatorDonationRepository;
import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;

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
	private final DonatorDonationRepository donatorDonationRepository;
	private final DonatorRepository donatorRepository;
	private final PasswordEncoder passwordEncoder;
	private Donator test;

	@BeforeEach
	void setup() {
		Donator test = new Donator();
		test.setUsername("donator_test");
		test.setPassword(passwordEncoder.encode("0000"));

		this.test = donatorRepository.save(test);

		createMockDonation(this.test, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID);
		createMockDonation(this.test, UUID.randomUUID(), new BigDecimal("150.00"), PaymentStatus.PAID);
	}

	private void createMockDonation(Donator donator, UUID id, BigDecimal amount, PaymentStatus status) {
		DonatorDonation donation = new DonatorDonation();
		donation.setDonator(donator);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);

		donatorDonationRepository.save(donation);
	}

	@Test
	@WithMockDonator(username = "donator_mock")
	void donatorDonationSummaryFlowTest() throws Exception {
		Donator mock = donatorRepository.findByUsername("donator_mock");

		createMockDonation(mock, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID);
		createMockDonation(mock, UUID.randomUUID(), new BigDecimal("50.00"), PaymentStatus.PAID);
		createMockDonation(mock, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PENDING);

		MvcResult result = mockMvc.perform(get("/api/donator/donation/sum")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		DonatorDonationSum sum = objectMapper.readValue(response, DonatorDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("150.00"), sum.total());
	}

}
