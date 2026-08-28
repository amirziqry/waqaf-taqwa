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
import com.taqwa.gowaqaf.mockuser.donator.WithMockPersonal;
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.vendor.WithMockMerchant;
import com.taqwa.gowaqaf.modules.agent.component.AgentStatus;
import com.taqwa.gowaqaf.modules.agent.component.AgentType;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrInfo;
import com.taqwa.gowaqaf.modules.agent.dto.RakanQrStatusRequest;
import com.taqwa.gowaqaf.modules.agent.entity.RakanQr;
import com.taqwa.gowaqaf.modules.agent.repository.RakanQrRepository;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountIdentityRepository;
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
	private final AccountIdentityRepository identityRepository;
	private final RakanQrRepository agentRepository;
	private final PasswordEncoder passwordEncoder;

	private RakanQr test;

	@BeforeEach
	void setup() {
		createMockAgent(CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"merchant1", "merchant1@gmail.com"), null, AgentType.MERCHANT, AgentStatus.ACTIVE);
		createMockAgent(CommonClass.createMockMerchant(merchantRepository, identityRepository, passwordEncoder,
				"merchant2", "merchant2@gmail.com"), null, AgentType.MERCHANT, AgentStatus.ACTIVE);
		createMockAgent(null, CommonClass.createMockPersonal(personalRepository, identityRepository, passwordEncoder,
				"personal1", "personal1@gmail.com"), AgentType.PERSONAL, AgentStatus.ACTIVE);
		this.test = createMockAgent(null, CommonClass.createMockPersonal(personalRepository, identityRepository,
				passwordEncoder, "personal2", "personal2@gmail.com"), AgentType.PERSONAL, AgentStatus.PENDING);
	}

	private RakanQr createMockAgent(Merchant merchant, Personal personal, AgentType type, AgentStatus status) {
		RakanQr agent = new RakanQr();

		agent.setType(type);
		agent.setStatus(status);

		if (type == AgentType.MERCHANT)
			agent.setMerchant(merchant);

		if (type == AgentType.PERSONAL)
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
		assertEquals(AgentType.MERCHANT, responseObject.getType());
		assertEquals(AgentStatus.PENDING, responseObject.getStatus());
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
		assertEquals(AgentType.PERSONAL, responseObject.getType());
		assertEquals(AgentStatus.PENDING, responseObject.getStatus());
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

		assertTrue(agents.stream().allMatch(agent -> agent.getStatus() == AgentStatus.ACTIVE));

		RakanQrStatusRequest request = new RakanQrStatusRequest(AgentStatus.ACTIVE);

		mockMvc.perform(patch("/api/rakan-qr-agent/{id}/status", test.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

		// Second request: now all 4 should be ACTIVE
		result = mockMvc.perform(get("/api/rakan-qr-agent/get/all").param("status", "ACTIVE"))
				.andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		agents = objectMapper.readValue(response, new TypeReference<List<RakanQrInfo>>() {
		});

		assertEquals(4, agents.size());

		assertTrue(agents.stream().allMatch(agent -> agent.getStatus() == AgentStatus.ACTIVE));

		result = mockMvc.perform(get("/api/rakan-qr-agent/get/all")).andExpect(status().isOk()).andReturn();

		response = result.getResponse().getContentAsString();

		agents = objectMapper.readValue(response, new TypeReference<List<RakanQrInfo>>() {
		});

		assertEquals(4, agents.size());
	}

}
