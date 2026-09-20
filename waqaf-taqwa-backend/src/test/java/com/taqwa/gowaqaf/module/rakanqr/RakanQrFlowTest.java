package com.taqwa.gowaqaf.module.rakanqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.enums.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class RakanQrFlowTest {

	private final MockMvc mockMvc;

	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	private final RakanQrRepository rakanQrRepository;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private RakanQr test;

	@BeforeEach
	void setup() {
		Account p1 = CommonClass.createMockPersonal(accountRepository, passwordEncoder, "personal1",
				"personal1@gmail.com");
		Account p2 = CommonClass.createMockPersonal(accountRepository, passwordEncoder, "personal2",
				"personal2@gmail.com");

		CommonClass.createMockRakanQr(rakanQrRepository, p1, RakanQrType.DUTA, RakanQrStatus.ACTIVE);
		this.test = CommonClass.createMockRakanQr(rakanQrRepository, p2, RakanQrType.AHLI, RakanQrStatus.ACTIVE);
		CommonClass.createMockRakanQr(rakanQrRepository, p1, RakanQrType.DUTA, RakanQrStatus.ACTIVE);
		CommonClass.createMockRakanQr(rakanQrRepository, p1, RakanQrType.AHLI, RakanQrStatus.PENDING);
	}

	@Test
	@WithMockPersonal(username = "personalmock")
	void personalRakanQrTest() throws Exception {
		String requestBody = """
				{
					"type": "STANDARD"
				}
				""";
		MvcResult result = mockMvc
				.perform(post("/api/rakan-qr/apply").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		RakanQrInfo object = objectMapper.readValue(response, RakanQrInfo.class);

		assertNotNull(object);
		assertNotNull(object.getId());
		assertNotNull(object.getIcNumber());
		assertEquals(RakanQrType.AHLI, object.getType());
		assertEquals(RakanQrStatus.PENDING, object.getStatus());
		assertEquals(new BigDecimal("0.00"), object.getCollectedAmount());
		assertNull(object.getCommission());
	}

	@Test
	@WithMockAdmin(username = "admin", roles = { "ADMIN" })
	void adminGetActiveRakanQrTest() throws Exception {
		String requestBody = """
				{
					"status": "ACTIVE"
				}
				""";
		mockMvc.perform(patch("/api/rakan-qr/{id}/status/update", test.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)).andExpect(status().isOk());

		assertEquals(RakanQrStatus.ACTIVE, rakanQrRepository.findById(test.getId()).get().getStatus());
	}

	@Test
	@WithMockAdmin(username = "admin", roles = { "ADMIN" })
	void adminGetAllRakanQrTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/rakan-qr/all/get").param("page", "0").param("size", "10"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		System.out.println(response);

		JsonNode json = objectMapper.readTree(response);

		assertEquals(4, json.get("content").size());
		assertEquals(4, json.get("page").get("totalElements").asInt());
		assertEquals(1, json.get("page").get("totalPages").asInt());
		assertEquals(0, json.get("page").get("number").asInt());
		assertEquals(10, json.get("page").get("size").asInt());
	}

	@Disabled
	@Test
	@WithMockAdmin(username = "admin", roles = { "ADMIN" })
	void adminGetFilteredRakanQrTest() throws Exception {

		MvcResult result = mockMvc.perform(get("/api/rakan-qr/all/get").param("type", "AMBASSADOR")
				.param("status", "ACTIVE").param("page", "0").param("size", "10")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		JsonNode json = objectMapper.readTree(response);

		assertEquals(2, json.get("content").size());
		assertEquals(2, json.get("page").get("totalElements").asInt());
		assertEquals(1, json.get("page").get("totalPages").asInt());

		List<RakanQrInfo> agents = objectMapper.readValue(json.get("content").toString(),
				new TypeReference<List<RakanQrInfo>>() {
				});

		assertTrue(agents.stream().allMatch(agent -> agent.getType() == RakanQrType.DUTA));
		assertTrue(agents.stream().allMatch(agent -> agent.getStatus() == RakanQrStatus.ACTIVE));
	}

}
