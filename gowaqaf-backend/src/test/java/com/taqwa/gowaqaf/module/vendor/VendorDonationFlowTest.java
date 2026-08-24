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
import com.taqwa.gowaqaf.mockuser.vendor.WithMockVendor;
import com.taqwa.gowaqaf.modules.donation.donator.dto.DonatorDonationSum;
import com.taqwa.gowaqaf.modules.donation.donator.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.vendor.entity.VendorDonation;
import com.taqwa.gowaqaf.modules.donation.vendor.repository.VendorDonationRepository;
import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;

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
	private final VendorDonationRepository vendorDonationRepository;
	private final VendorRepository vendorRepository;
	private final PasswordEncoder passwordEncoder;
	private Vendor test;

	@BeforeEach
	void setup() {
		Vendor test = new Vendor();
		test.setUsername("donator_test");
		test.setPassword(passwordEncoder.encode("0000"));

		this.test = vendorRepository.save(test);

		createMockVendor(this.test, UUID.randomUUID(), new BigDecimal("1.50"), PaymentStatus.PAID);
		createMockVendor(this.test, UUID.randomUUID(), new BigDecimal("3.50"), PaymentStatus.PAID);
	}

	private void createMockVendor(Vendor vendor, UUID id, BigDecimal amount, PaymentStatus status) {
		VendorDonation donation = new VendorDonation();
		donation.setVendor(vendor);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);

		vendorDonationRepository.save(donation);
	}

	@Test
	@WithMockVendor(username = "vendor_mock")
	void vendorDonationSummaryFlowTest() throws Exception {
		Vendor mock = vendorRepository.findByUsername("vendor_mock");

		createMockVendor(mock, UUID.randomUUID(), new BigDecimal("2.50"), PaymentStatus.PAID);
		createMockVendor(mock, UUID.randomUUID(), new BigDecimal("3.00"), PaymentStatus.PAID);
		createMockVendor(mock, UUID.randomUUID(), new BigDecimal("2.50"), PaymentStatus.PENDING);

		MvcResult result = mockMvc.perform(get("/api/vendor/donation/sum")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		DonatorDonationSum sum = objectMapper.readValue(response, DonatorDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("5.50"), sum.total());
	}

}
