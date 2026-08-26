package com.taqwa.gowaqaf.module.vendor;

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
import com.taqwa.gowaqaf.mockuser.vendor.WithMockMerchant;
import com.taqwa.gowaqaf.modules.donation.merchant.entity.MerchantDonation;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class VendorDonationFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final MerchantDonationRepository merchantDonationRepository;
	private final MerchantRepository merchantRepository;
	private final PasswordEncoder passwordEncoder;
	private Merchant test;

	@BeforeEach
	void setup() {
		Merchant test = new Merchant();
		test.setUsername("donator_test");
		test.setPassword(passwordEncoder.encode("0000"));

		this.test = merchantRepository.save(test);

		createMockMerchant(this.test, UUID.randomUUID(), new BigDecimal("1.50"), PaymentStatus.PAID);
		createMockMerchant(this.test, UUID.randomUUID(), new BigDecimal("3.50"), PaymentStatus.PAID);
	}

	private void createMockMerchant(Merchant merchant, UUID id, BigDecimal amount, PaymentStatus status) {
		MerchantDonation donation = new MerchantDonation();
		donation.setMerchant(merchant);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);

		merchantDonationRepository.save(donation);
	}

	@Test
	@WithMockMerchant(username = "vendor_mock")
	void vendorDonationSummaryFlowTest() throws Exception {
		Merchant mock = merchantRepository.findByUsername("vendor_mock").get();

		createMockMerchant(mock, UUID.randomUUID(), new BigDecimal("2.50"), PaymentStatus.PAID);
		createMockMerchant(mock, UUID.randomUUID(), new BigDecimal("3.00"), PaymentStatus.PAID);
		createMockMerchant(mock, UUID.randomUUID(), new BigDecimal("2.50"), PaymentStatus.PENDING);

		MvcResult result = mockMvc.perform(get("/api/vendor/donation/sum")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		PersonalDonationSum sum = objectMapper.readValue(response, PersonalDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("5.50"), sum.total());
	}

}
