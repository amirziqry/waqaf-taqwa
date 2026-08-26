package com.taqwa.gowaqaf.module.member;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
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

import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.entity.Role;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class MemberFlowTest {

	private final MockMvc mockMvc;
	private final AdminRepository adminRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {

		adminRepository.deleteAll();

		Admin admin1 = new Admin(null, "member1", "member1@gmail.com", passwordEncoder.encode("0000"),
				Set.of(Role.EDITOR));

		Admin admin2 = new Admin(null, "member2", "member2@gmail.com", passwordEncoder.encode("0000"),
				Set.of(Role.EDITOR));

		Admin admin3 = new Admin(null, "member3", "member3@gmail.com", passwordEncoder.encode("0000"),
				Set.of(Role.ADMIN));

		Admin admin4 = new Admin(null, "member4", "member4@gmail.com", passwordEncoder.encode("0000"),
				Set.of(Role.EDITOR));

		adminRepository.saveAll(List.of(admin1, admin2, admin3, admin4));
	}

	@Test
	@WithMockAdmin(username = "supermember", roles = { "ADMIN" })
	void memberFlowTest() throws Exception {

		mockMvc.perform(get("/api/member/get/" + "member1")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.email").value("member1@gmail.com"))
				.andExpect(jsonPath("$.roles").value(hasItem("EDITOR")));

		mockMvc.perform(get("/api/member/get/all")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(4))).andExpect(jsonPath("$[*].username",
						Matchers.containsInAnyOrder("member1", "member2", "member3", "member4")));

		String requestJson = """
				{
					"role": "ADMIN"
				}
				""";

		mockMvc.perform(
				patch("/api/member/update/member2/role").contentType(MediaType.APPLICATION_JSON).content(requestJson))
				.andExpect(MockMvcResultMatchers.status().isOk());

		mockMvc.perform(get("/api/member/get/" + "member2")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.email").value("member2@gmail.com"))
				.andExpect(jsonPath("$.roles.length()").value(1))
				.andExpect(jsonPath("$.roles").value(hasItem("ADMIN")));
	}

}
