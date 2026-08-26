package com.taqwa.gowaqaf.module.organization;

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
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.modules.donation.merchant.entity.MerchantDonation;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.entity.DonationType;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
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
public class OrganizationDonationSumFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final PersonalRepository personalRepository;
	private final MerchantRepository merchantRepository;
	private final PersonalDonationRepository personalDonationRepository;
	private final MerchantDonationRepository merchantDonationRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Personal d1 = createTestPersonal("donator1", "0000");
		Personal d2 = createTestPersonal("donator2", "0000");

		createMockPersonalDonation(d1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 1, 10, 0, 0));
		createMockPersonalDonation(d1, UUID.randomUUID(), new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 10, 14, 30, 0));
		createMockPersonalDonation(d2, UUID.randomUUID(), new BigDecimal("25.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 15, 18, 0, 0));
		createMockPersonalDonation(d1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PENDING,
				LocalDateTime.of(2026, 8, 20, 12, 0, 0));

		Merchant v1 = createTestMerchant("vendor1", "0000");
		Merchant v2 = createTestMerchant("vendor2", "0000");

		createMockMerchantDonation(v1, UUID.randomUUID(), new BigDecimal("200.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 5, 10, 0, 0));
		createMockMerchantDonation(v1, UUID.randomUUID(), new BigDecimal("50.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 12, 15, 0, 0));
		createMockMerchantDonation(v2, UUID.randomUUID(), new BigDecimal("25.00"), PaymentStatus.PAID,
				LocalDateTime.of(2026, 8, 18, 20, 0, 0));
		createMockMerchantDonation(v1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PENDING,
				LocalDateTime.of(2026, 8, 25, 12, 0, 0));
	}

	private Personal createTestPersonal(String username, String password) {
		Personal user = new Personal();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		return personalRepository.save(user);
	}

	private Merchant createTestMerchant(String username, String password) {
		Merchant user = new Merchant();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		return merchantRepository.save(user);
	}

	private void createMockPersonalDonation(Personal personal, UUID id, BigDecimal amount, PaymentStatus status,
			LocalDateTime paidAt) {
		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);
		donation.setDonationType(DonationType.DIRECT);

		personalDonationRepository.save(donation);
	}

	private void createMockMerchantDonation(Merchant merchant, UUID id, BigDecimal amount, PaymentStatus status,
			LocalDateTime paidAt) {
		MerchantDonation donation = new MerchantDonation();
		donation.setMerchant(merchant);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setPaidAt(paidAt);
		donation.setDonationType(DonationType.DIRECT);

		merchantDonationRepository.save(donation);
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void orgDonationFlowTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/organization/donation/sum")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		OrganizationDonationSum sum = objectMapper.readValue(response, OrganizationDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("175.00"), sum.donatorTotal());
		assertEquals(new BigDecimal("275.00"), sum.vendorTotal());
		assertEquals(new BigDecimal("450.00"), sum.total());
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void orgDonationFlowWithDateRangeTest() throws Exception {

		MvcResult result = mockMvc.perform(
				get("/api/organization/donation/sum").param("startDate", "05-08-2026").param("endDate", "15-08-2026"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		OrganizationDonationSum sum = objectMapper.readValue(response, OrganizationDonationSum.class);

		assertNotNull(sum);

		assertEquals(new BigDecimal("75.00"), sum.donatorTotal());
		assertEquals(new BigDecimal("250.00"), sum.vendorTotal());
		assertEquals(new BigDecimal("325.00"), sum.total());
	}

}
