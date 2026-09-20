package com.taqwa.gowaqaf.module.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

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
import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.common.CommonEndpoints;
import com.taqwa.gowaqaf.external.payment.dto.PaymentUrlResponse;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class ProjectPaymentFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	private final ProjectRepository projectRepository;

	Project p1;

	@BeforeEach
	void setup() {
		this.p1 = CommonClass.createMockProject(projectRepository, "Project1", new BigDecimal("10000"),
				ContentStatus.PUBLISHED);
		CommonClass.createMockProject(projectRepository, "Project2", new BigDecimal("10000"), ContentStatus.PUBLISHED);
	}

	@Test
	@WithMockPersonal(username = "personalmock")
	void projectPaymentFlowTest() throws Exception {

		String requestJson = """
				{
				    "amount": 10.00,
				    "taxExempt": false
				}
				""";

		MvcResult result = mockMvc
				.perform(post(CommonEndpoints.projectDonationRequest, p1.getId().toString())
						.contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.billingCode").isNotEmpty()).andExpect(jsonPath("$.amount").value(10.0))
				.andExpect(jsonPath("$.status").value(PaymentStatus.UNPAID.toString()))
				.andExpect(jsonPath("$.paymentUrl").isNotEmpty()).andReturn();

		String response = result.getResponse().getContentAsString();
		PaymentUrlResponse paymentResponse = objectMapper.readValue(response, PaymentUrlResponse.class);

		assertNotNull(paymentResponse);
		assertNotNull(paymentResponse.getId());
		assertNotNull(paymentResponse.getBillingCode());
		assertNotNull(paymentResponse.getPaymentUrl());

		System.out.println("========================================");
		System.out.println("Payment URL: " + paymentResponse.getPaymentUrl());
		System.out.println("Billing Code: " + paymentResponse.getBillingCode());
		System.out.println("Donation ID: " + paymentResponse.getId());
		System.out.println("========================================");

		System.out.println();
		System.out.println("Open the Payment URL and complete the sandbox payment.");
		System.out.println("Then verify that the webhook reaches the application.");
		System.out.println();
		System.out.println("Press ENTER to continue the test...");

		new BufferedReader(new InputStreamReader(System.in)).readLine();

		result = mockMvc.perform(get(CommonEndpoints.projectGetDonationDetails, paymentResponse.getId())).andDo(print())
				.andReturn();

		response = result.getResponse().getContentAsString();

		JsonNode json = objectMapper.readTree(response);

		assertNotNull(json.get("id"));
		assertNotNull(json.get("billingCode"));
		assertNotNull(json.get("transactionId"));
		assertNotNull(json.get("amount"));
		assertNotNull(json.get("paidAt"));
		assertNotNull(json.get("status"));
		assertNotNull(json.get("projectId"));
		assertNotNull(json.get("projectName"));

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
		System.out.println("Project ID:      " + json.get("projectId").asText());
		System.out.println("Project Name:    " + json.get("projectName").asText());
		System.out.println("========================================");

		Project project = projectRepository.findById(p1.getId()).orElseThrow();
		assertEquals(new BigDecimal("10.00"), project.getCollectedAmount());

	}

}
