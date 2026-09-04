package com.taqwa.gowaqaf.module.personal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.common.CommonEndpoints;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class PersonalFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	@BeforeEach
	void setup() {

	}

	@Test
	void personalUserFlowTest() throws Exception {
		// ////////////////
		// Register
		// ///////////////
		String requestBody = """
					{
				        "username": "personalmock",
				        "email": "personalmock@gmail.com",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(
				post(CommonEndpoints.personalRegister).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("personalmock"));

		// ////////////////
		// Login
		// ///////////////
		requestBody = """
					{
				        "username": "personalmock",
				        "password": "0000"
				    }
				""";

		MvcResult response = mockMvc
				.perform(post(CommonEndpoints.personalLogin).contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("personalmock")).andReturn();

		Cookie accessToken = response.getResponse().getCookie("accessToken");
		assertNotNull(accessToken);

		mockMvc.perform(get(CommonEndpoints.personalMe).cookie(accessToken)).andExpect(status().isOk());

		// ////////////////
		// Get Account
		// ///////////////
		mockMvc.perform(get(CommonEndpoints.personalGetAccount).cookie(accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("personalmock"))
				.andExpect(jsonPath("$.username").value("personalmock"))
				.andExpect(jsonPath("$.accountHolderName").isEmpty())
				.andExpect(jsonPath("$.email").value("personalmock@gmail.com")).andExpect(jsonPath("$.phone").isEmpty())
				.andExpect(jsonPath("$.modMesra").value(false));

		// ////////////////
		// Update Account
		// ///////////////
		requestBody = """
					{
				        "accountHolderName": "John Doe",
				        "email": "personalmock@gmail.com",
				        "phone": "60123456789",
				        "modMesra": false
				    }
				""";

		mockMvc.perform(put(CommonEndpoints.personalUpdateAccount).cookie(accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk());

		// ////////////////
		// Get Account
		// ///////////////
		mockMvc.perform(get(CommonEndpoints.personalGetAccount).cookie(accessToken)
				.contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("personalmock"))
				.andExpect(jsonPath("$.accountHolderName").value("John Doe"))
				.andExpect(jsonPath("$.email").value("personalmock@gmail.com"))
				.andExpect(jsonPath("$.phone").value("60123456789")).andExpect(jsonPath("$.modMesra").value(false));

		// ////////////////
		// Test Donation
		// ///////////////

		String requestJson = """
				{
				    "amount": 10.00,
				    "taxExempt": false
				}
				""";

		response = mockMvc
				.perform(post(CommonEndpoints.personalDirectDonationRequest).cookie(accessToken)
						.contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.billingCode").isNotEmpty()).andExpect(jsonPath("$.amount").value(10.0))
				.andExpect(jsonPath("$.status").value(PaymentStatus.UNPAID.toString()))
				.andExpect(jsonPath("$.paymentUrl").isNotEmpty()).andReturn();

		String donationResponse = response.getResponse().getContentAsString();
		PaymentUrlResponse paymentObject = objectMapper.readValue(donationResponse, PaymentUrlResponse.class);

		assertNotNull(paymentObject);
		assertNotNull(paymentObject.getId());
		assertNotNull(paymentObject.getBillingCode());
		assertNotNull(paymentObject.getPaymentUrl());

		System.out.println("========================================");
		System.out.println("Payment URL: " + paymentObject.getPaymentUrl());
		System.out.println("Billing Code: " + paymentObject.getBillingCode());
		System.out.println("Donation ID: " + paymentObject.getId());
		System.out.println("========================================");

		System.out.println();
		System.out.println("Open the Payment URL and complete the sandbox payment.");
		System.out.println("Then verify that the webhook reaches the application.");
		System.out.println();
		System.out.println("Press ENTER to continue the test...");

		new BufferedReader(new InputStreamReader(System.in)).readLine();

		response = mockMvc.perform(
				get(CommonEndpoints.personalPaymentStatus(paymentObject.getId().toString())).cookie(accessToken))
				.andDo(print()).andReturn();

		String statusResponse = response.getResponse().getContentAsString();

		JsonNode json = objectMapper.readTree(statusResponse);

		assertNotNull(json.get("id"));
		assertNotNull(json.get("billingCode"));
		assertNotNull(json.get("transactionId"));
		assertNotNull(json.get("amount"));
		assertNotNull(json.get("paidAt"));
		assertNotNull(json.get("status"));

		assertEquals(PaymentStatus.PAID.toString(), json.get("status").asText());

		System.out.println("========================================");
		System.out.println("Payment Status Details");
		System.out.println("========================================");
		System.out.println("ID:              " + json.get("id").asText());
		System.out.println("Billing Code:    " + json.get("billingCode").asText());
		System.out.println("Transaction ID:  " + json.get("transactionId").asText());
		System.out.println("Amount:          " + json.get("amount").asText());
		System.out.println("Paid At:         " + json.get("paidAt").asText());
		System.out.println("Status:          " + json.get("status").asText());
		System.out.println("Receipt Hash ID: " + json.get("receiptHashId").asText());
		System.out.println("========================================");
	}

}
