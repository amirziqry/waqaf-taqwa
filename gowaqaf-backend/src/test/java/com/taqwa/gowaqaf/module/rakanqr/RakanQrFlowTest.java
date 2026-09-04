package com.taqwa.gowaqaf.module.rakanqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.merchant.WithMockMerchant;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrStatus;
import com.taqwa.gowaqaf.modules.feature.rakanqr.component.RakanQrType;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.feature.rakanqr.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.feature.rakanqr.entity.RakanQr;
import com.taqwa.gowaqaf.modules.feature.rakanqr.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
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
public class RakanQrFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;
	private final MerchantRepository merchantRepository;
	private final PersonalRepository personalRepository;
	private final AccountInfoRepository identityRepository;
	private final RakanQrRepository agentRepository;
	private final PasswordEncoder passwordEncoder;

	private RakanQr test;

	@BeforeEach
	void setup() {
		createMockAgent(CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"merchant1", "merchant1@gmail.com"), null, RakanQrType.MERCHANT, RakanQrStatus.ACTIVE);
		createMockAgent(CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"merchant2", "merchant2@gmail.com"), null, RakanQrType.MERCHANT, RakanQrStatus.ACTIVE);
		createMockAgent(null, CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"personal1", "personal1@gmail.com"), RakanQrType.PERSONAL, RakanQrStatus.ACTIVE);
		this.test = createMockAgent(null, CommonClass.createMockPersonal(personalRepository, identityRepository,
				passwordEncoder, "personal2", "personal2@gmail.com"), RakanQrType.PERSONAL, RakanQrStatus.PENDING);
	}

	private RakanQr createMockAgent(Merchant merchant, Personal personal, RakanQrType type, RakanQrStatus status) {
		RakanQr agent = new RakanQr();

		agent.setType(type);
		agent.setStatus(status);

		if (type == RakanQrType.MERCHANT)
			agent.setMerchant(merchant);

		if (type == RakanQrType.PERSONAL)
			agent.setPersonal(personal);

		return agentRepository.save(agent);
	}

	@Test
	@WithMockMerchant(username = "merchantmock")
	void merchantAgentTest() throws Exception {

		MvcResult result = mockMvc.perform(post("/api/rakan-qr-agent/apply")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		RakanQrInfo responseObject = objectMapper.readValue(response, RakanQrInfo.class);

		assertNotNull(responseObject);
		assertNotNull(responseObject.getId());
		assertEquals("test@gmail.com", responseObject.getEmail());
		assertEquals(RakanQrType.MERCHANT, responseObject.getType());
		assertEquals(RakanQrStatus.PENDING, responseObject.getStatus());
	}

	@Test
	@WithMockPersonal(username = "personalmock")
	void personalAgentTest() throws Exception {
		MvcResult result = mockMvc.perform(post("/api/rakan-qr-agent/apply")).andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		RakanQrInfo responseObject = objectMapper.readValue(response, RakanQrInfo.class);

		assertNotNull(responseObject);
		assertNotNull(responseObject.getId());
		assertEquals("test@gmail.com", responseObject.getEmail());
		assertEquals(RakanQrType.PERSONAL, responseObject.getType());
		assertEquals(RakanQrStatus.PENDING, responseObject.getStatus());
	}

	@Test
	@WithMockAdmin(username = "admin", roles = { "ADMIN" })
	void adminGetActiveRakanQrTest() throws Exception {

		// First request: only the 3 ACTIVE agents
		MvcResult result = mockMvc.perform(get("/api/rakan-qr-agent/get/all").param("status", "ACTIVE"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		List<RakanQrInfo> agents = objectMapper.readValue(response, new TypeReference<List<RakanQrInfo>>() {
		});

		assertEquals(3, agents.size());

		assertTrue(agents.stream().allMatch(agent -> agent.getStatus() == RakanQrStatus.ACTIVE));

		RakanQrStatusRequest request = new RakanQrStatusRequest(RakanQrStatus.ACTIVE);

		mockMvc.perform(patch("/api/rakan-qr-agent/{id}/status", test.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

		// Second request: now all 4 should be ACTIVE
		result = mockMvc.perform(get("/api/rakan-qr-agent/get/all").param("status", "ACTIVE"))
				.andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		agents = objectMapper.readValue(response, new TypeReference<List<RakanQrInfo>>() {
		});

		assertEquals(4, agents.size());

		assertTrue(agents.stream().allMatch(agent -> agent.getStatus() == RakanQrStatus.ACTIVE));

		result = mockMvc.perform(get("/api/rakan-qr-agent/get/all")).andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		agents = objectMapper.readValue(response, new TypeReference<List<RakanQrInfo>>() {
		});

		assertEquals(4, agents.size());
	}

}
