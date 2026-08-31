package com.taqwa.gowaqaf.module.donation.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.modules.donation.enums.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectCollectionSum;
import com.taqwa.gowaqaf.modules.organization.content.enums.ContentStatus;
import com.taqwa.gowaqaf.modules.organization.content.project.dto.ProjectDetails;
import com.taqwa.gowaqaf.modules.organization.content.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.content.project.repository.ProjectRepository;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class ProjectDonationSumTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	private final PersonalDonationRepository personalDonationRepository;

	private final ProjectRepository projectRepository;

	private final AccountInfoRepository infoRepository;
	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;

	Personal p1, p2;
	Project d1, d2;

	@BeforeEach
	void setup() {
		this.p1 = CommonClass.createMockPersonal(personalRepository, infoRepository, passwordEncoder, "donator1",
				"test@gmail.com");
		this.p2 = CommonClass.createMockPersonal(personalRepository, infoRepository, passwordEncoder, "donator2",
				"test@gmail.com");

		this.d1 = CommonClass.createMockProject(projectRepository, "Project1", new BigDecimal("10000.00"),
				ContentStatus.PUBLISHED);
		this.d2 = CommonClass.createMockProject(projectRepository, "Project2", new BigDecimal("10000.00"),
				ContentStatus.PUBLISHED);

		CommonClass.createMockProjectDonation(personalDonationRepository, p1, d1, new BigDecimal("100.00"),
				PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockProjectDonation(personalDonationRepository, p2, d1, new BigDecimal("150.00"),
				PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockProjectDonation(personalDonationRepository, p1, d2, new BigDecimal("100.00"),
				PaymentStatus.PAID, LocalDateTime.now());
		CommonClass.createMockProjectDonation(personalDonationRepository, p2, d2, new BigDecimal("200.00"),
				PaymentStatus.PAID, LocalDateTime.now());
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void projectSumFlowTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/project/donation/" + d1.getId() + "/collection"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		ProjectCollectionSum sum = objectMapper.readValue(response, ProjectCollectionSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("250.00"), sum.total());

		result = mockMvc.perform(get("/api/project/donation/" + d2.getId() + "/collection")).andExpect(status().isOk())
				.andReturn();

		response = result.getResponse().getContentAsString();

		sum = objectMapper.readValue(response, ProjectCollectionSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("300.00"), sum.total());
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void getAllProjectWithCollectionFlowTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/organization/project/all/get")).andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		List<ProjectDetails> projects = objectMapper.readValue(response,
				objectMapper.getTypeFactory().constructCollectionType(List.class, ProjectDetails.class));

		assertEquals(2, projects.size());

		ProjectDetails project1 = projects.stream().filter(project -> project.getName().equals("Project1")).findFirst()
				.orElseThrow();

		ProjectDetails project2 = projects.stream().filter(project -> project.getName().equals("Project2")).findFirst()
				.orElseThrow();

		assertEquals(new BigDecimal("250.00"), project1.getCollectedAmount());
		assertEquals(new BigDecimal("300.00"), project2.getCollectedAmount());

	}

}
