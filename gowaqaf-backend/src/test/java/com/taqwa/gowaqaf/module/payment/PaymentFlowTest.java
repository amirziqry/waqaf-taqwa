package com.taqwa.gowaqaf.module.payment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.payment.dto.PaymentUrlResponse;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class PaymentFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	@BeforeEach
	void setup() {

	}

	@Test
	@WithMockAdmin(username = "member1", roles = { "ADMIN" })
	void donatorPaymentFlowTest() throws Exception {

		String requestJson = """
				{
				    "amount": 10.00
				}
				""";

		MvcResult donationResult = mockMvc
				.perform(post("/api/donator/payment/request-gateway-url").contentType(MediaType.APPLICATION_JSON)
						.content(requestJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.billingCode").isNotEmpty())
				.andExpect(jsonPath("$.status").value(PaymentStatus.UNPAID.toString()))
				.andExpect(jsonPath("$.paymentUrl").value("https://system-nexgen.test/test-payment")).andReturn();

		String donationResponse = donationResult.getResponse().getContentAsString();

		PaymentUrlResponse paymentResponse = objectMapper.readValue(donationResponse, PaymentUrlResponse.class);

		assertNotNull(paymentResponse);
		assertNotNull(paymentResponse.getId());
		assertNotNull(paymentResponse.getBillingCode());
		assertNotNull(paymentResponse.getPaymentUrl());

	}

}
