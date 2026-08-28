package com.taqwa.gowaqaf.module.admin;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountIdentityRepository;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class AdminFlowTest {

	private final MockMvc mockMvc;
	private final AdminRepository adminRepository;
	private final AccountIdentityRepository identityRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		CommonClass.createMockAdmin(adminRepository, identityRepository, passwordEncoder, "member1",
				"member1@gmail.com", Set.of(Role.EDITOR));
		CommonClass.createMockAdmin(adminRepository, identityRepository, passwordEncoder, "member2",
				"member2@gmail.com", Set.of(Role.EDITOR));
		CommonClass.createMockAdmin(adminRepository, identityRepository, passwordEncoder, "member3",
				"member3@gmail.com", Set.of(Role.ADMIN));
		CommonClass.createMockAdmin(adminRepository, identityRepository, passwordEncoder, "member4",
				"member4@gmail.com", Set.of(Role.EDITOR));
	}

	@Test
	@WithMockAdmin(username = "supermember", roles = { "ADMIN" })
	void memberFlowTest() throws Exception {

		mockMvc.perform(get("/api/admin/get/" + "member1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.email").value("member1@gmail.com"))
				.andExpect(jsonPath("$.roles").value(hasItem("EDITOR")));

		mockMvc.perform(get("/api/admin/get/all")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(5))).andExpect(jsonPath("$[*].username",
						Matchers.containsInAnyOrder("member1", "member2", "member3", "member4", "supermember")));

		String requestJson = """
				{
					"role": "ADMIN"
				}
				""";

		mockMvc.perform(
				patch("/api/admin/update/member2/role").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(MockMvcResultMatchers.status().isOk());

		mockMvc.perform(get("/api/admin/get/" + "member2")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.email").value("member2@gmail.com"))
				.andExpect(jsonPath("$.roles.length()").value(1))
				.andExpect(jsonPath("$.roles").value(hasItem("ADMIN")));
	}

}
