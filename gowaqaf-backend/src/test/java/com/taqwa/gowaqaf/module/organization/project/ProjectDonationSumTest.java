package com.taqwa.gowaqaf.module.organization.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.modules.donation.personal.entity.DonationType;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PaymentStatus;
import com.taqwa.gowaqaf.modules.donation.personal.entity.PersonalDonation;
import com.taqwa.gowaqaf.modules.donation.personal.repository.PersonalDonationRepository;
import com.taqwa.gowaqaf.modules.donation.project.dto.ProjectDonationSum;
import com.taqwa.gowaqaf.modules.organization.project.component.category.entity.ProjectCategory;
import com.taqwa.gowaqaf.modules.organization.project.component.category.repository.ProjectCategoryRepository;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.entity.ProjectTag;
import com.taqwa.gowaqaf.modules.organization.project.component.tag.repository.ProjectTagRepository;
import com.taqwa.gowaqaf.modules.organization.project.entity.Project;
import com.taqwa.gowaqaf.modules.organization.project.entity.Status;
import com.taqwa.gowaqaf.modules.organization.project.repository.ProjectRepository;
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
	private final ProjectCategoryRepository categoryRepository;
	private final ProjectTagRepository tagRepository;

	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;

	Project p1, p2;
	Personal d1, d2;

	@BeforeEach
	void setup() {
		this.d1 = createTestPersonal("donator1", "0000");
		this.d2 = createTestPersonal("donator2", "0000");

		this.p1 = createTestProject("Project1");
		this.p2 = createTestProject("Project2");

		createMockProjectDonation(d1, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID, p1);
		createMockProjectDonation(d2, UUID.randomUUID(), new BigDecimal("150.00"), PaymentStatus.PAID, p1);
		createMockProjectDonation(d1, UUID.randomUUID(), new BigDecimal("200.00"), PaymentStatus.PAID, p2);
		createMockProjectDonation(d2, UUID.randomUUID(), new BigDecimal("100.00"), PaymentStatus.PAID, p2);
	}

	private Personal createTestPersonal(String username, String password) {
		Personal user = new Personal();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));

		return personalRepository.save(user);
	}

	private Project createTestProject(String name) {
		Project project = new Project();

		project.setName(name);
		project.setCategory(categoryRepository.save(new ProjectCategory(null, "Category1")));
		project.setTags(new HashSet<>(Set.of(tagRepository.save(new ProjectTag(null, "Tag1")))));
		project.setStatus(Status.PUBLISHED);
		project.setImages(new ArrayList<>());
		project.setPaymentCollectionCode(UUID.randomUUID().toString());

		return projectRepository.save(project);
	}

	private void createMockProjectDonation(Personal personal, UUID id, BigDecimal amount, PaymentStatus status,
			Project project) {
		PersonalDonation donation = new PersonalDonation();
		donation.setPersonal(personal);
		donation.setBillingCode(id.toString());
		donation.setAmount(amount);
		donation.setStatus(status);
		donation.setProject(project);
		donation.setDonationType(DonationType.PROJECT);

		personalDonationRepository.save(donation);
	}

	@Test
	@WithMockAdmin(username = "mock_member", roles = { "ADMIN" })
	void projectSumFlowTest() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/project/donation/" + p1.getId() + "/sum"))
				.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();

		ProjectDonationSum sum = objectMapper.readValue(response, ProjectDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("250.00"), sum.total());

		result = mockMvc.perform(get("/api/project/donation/" + p2.getId() + "/sum")).andExpect(status().isOk())
				.andReturn();

		response = result.getResponse().getContentAsString();

		sum = objectMapper.readValue(response, ProjectDonationSum.class);

		assertNotNull(sum);
		assertEquals(new BigDecimal("300.00"), sum.total());
	}

}
