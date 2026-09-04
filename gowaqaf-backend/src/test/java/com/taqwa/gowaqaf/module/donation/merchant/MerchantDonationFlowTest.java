package com.taqwa.gowaqaf.module.donation.merchant;

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
import com.taqwa.gowaqaf.mockuser.merchant.WithMockMerchant;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.merchant.repository.MerchantDonationRepository;
import com.taqwa.gowaqaf.modules.donation.personal.dto.PersonalDonationSum;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class MerchantDonationFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final MerchantDonationRepository merchantDonationRepository;
	private final AccountInfoRepository identityRepository;
	private final MerchantRepository merchantRepository;
	private final PasswordEncoder passwordEncoder;
	private Merchant test;

	@BeforeEach
	void setup() {
		this.test = CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"donator_test", "test@gmail.com");

		CommonClass.createMockMerchantDonation(merchantDonationRepository, this.test, new BigDecimal("1.50"),
				PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockMerchantDonation(merchantDonationRepository, this.test, new BigDecimal("3.50"),
				PaymentStatus.PAID, LocalDateTime.now());
	}

	@Test
	@WithMockMerchant(username = "vendor_mock")
	void vendorDonationSummaryFlowTest() throws Exception {
		Merchant mock = merchantRepository.findByUsername("vendor_mock").get();

		CommonClass.createMockMerchantDonation(merchantDonationRepository, mock, new BigDecimal("2.50"),
				PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockMerchantDonation(merchantDonationRepository, mock, new BigDecimal("3.00"),
				PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockMerchantDonation(merchantDonationRepository, mock, new BigDecimal("2.50"),
				PaymentStatus.UNPAID, LocalDateTime.now());

		MvcResult result = mockMvc.perform(get("/api/merchant/donation/sum")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		PersonalDonationSum sum = objectMapper.readValue(response, PersonalDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("5.50"), sum.total());
	}

}
