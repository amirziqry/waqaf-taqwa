package com.taqwa.gowaqaf.module.organization;

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
import com.taqwa.gowaqaf.mockuser.member.WithMockMember;
import com.taqwa.gowaqaf.modules.donation.donator.entity.DonatorDonation;
import com.taqwa.gowaqaf.modules.donation.donator.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.donator.repository.DonatorDonationRepository;
import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSum;
import com.taqwa.gowaqaf.modules.donation.vendor.entity.VendorDonation;
import com.taqwa.gowaqaf.modules.donation.vendor.repository.VendorDonationRepository;
import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;
import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;

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
	private final DonatorRepository donatorRepository;
	private final VendorRepository vendorRepository;
	private final DonatorDonationRepository donatorDonationRepository;
	private final VendorDonationRepository vendorDonationRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Donator d1 = createTestDonator("donator1", "0000");
		Donator d2 = createTestDonator("donator2", "0000");

		createMockDonatorDonation(d1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID);
		createMockDonatorDonation(d1, UUID.randomUUID(), new BigDecimal("50.00"), PaymentStatus.PAID);
		createMockDonatorDonation(d2, UUID.randomUUID(), new BigDecimal("25.00"), PaymentStatus.PAID);
		createMockDonatorDonation(d1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PENDING);

		Vendor v1 = createTestVendor("vendor1", "0000");
		Vendor v2 = createTestVendor("vendor2", "0000");

		createMockVendorDonation(v1, UUID.randomUUID(), new BigDecimal("200.00"), PaymentStatus.PAID);
		createMockVendorDonation(v1, UUID.randomUUID(), new BigDecimal("50.00"), PaymentStatus.PAID);
		createMockVendorDonation(v2, UUID.randomUUID(), new BigDecimal("25.00"), PaymentStatus.PAID);
		createMockVendorDonation(v1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PENDING);
	}

	private Donator createTestDonator(String username, String password) {
		Donator user = new Donator();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		return donatorRepository.save(user);
	}

	private Vendor createTestVendor(String username, String password) {
		Vendor user = new Vendor();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		return vendorRepository.save(user);
	}

	private void createMockDonatorDonation(Donator donator, UUID id, BigDecimal amount, PaymentStatus status) {
		DonatorDonation donation = new DonatorDonation();
		donation.setDonator(donator);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);

		donatorDonationRepository.save(donation);
	}

	private void createMockVendorDonation(Vendor vendor, UUID id, BigDecimal amount, PaymentStatus status) {
		VendorDonation donation = new VendorDonation();
		donation.setVendor(vendor);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);

		vendorDonationRepository.save(donation);
	}

	@Test
	@WithMockMember(username = "mock_member", roles = { "ADMIN" })
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

}
