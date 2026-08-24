package com.taqwa.gowaqaf.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.mockuser.member.WithMockMember;
import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.modules.user.member.entity.Role;
import com.taqwa.gowaqaf.modules.user.member.repository.MemberRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class MemberAuthFlowTest {

	private final MockMvc mockMvc;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Member member = new Member();

		member.setUsername("member_test");
		member.setPassword(passwordEncoder.encode("0000"));
		member.setRoles(Set.of(Role.ADMIN));

		memberRepository.save(member);
	}

	@Disabled
	@Test
	void memberRegisterAdminShouldSuccess() throws Exception {

		String jsonPost1 = """
					{
				        "username": "member1",
				        "email": "-",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/member/register-admin")
				.contentType(MediaType.APPLICATION_JSON).content(jsonPost1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("member1"));

		String jsonPost2 = """
						{
				        "username": "member1",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/member/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(jsonPost2)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("member1"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasItem("ROLE_ADMIN")));
	}

	@Disabled
	@Test
	void memberRegisterEditorShouldSuccess() throws Exception {

		String jsonPost1 = """
					{
				        "username": "member2",
				        "email": "-",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/member/register-editor")
				.contentType(MediaType.APPLICATION_JSON).content(jsonPost1))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("member2"));

		String jsonPost2 = """
						{
				        "username": "member2",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/member/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(jsonPost2)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("member2"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasItem("ROLE_EDITOR")))
				.andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.not(Matchers.hasItem("ROLE_ADMIN"))));
	}

	@Test
	void memberLoginShouldSuccess() throws Exception {
		String requestBody = """
						{
				        "username": "member_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/member/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("member_test"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.hasItem("ROLE_ADMIN")));
	}

	@Test
	void memberInvalidPasswordShouldFail() throws Exception {
		String requestBody = """
						{
				        "username": "member_test",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/member/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(requestBody)).andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	void memberCanAccessEndpointAfterLogin() throws Exception {
		String requestBody = """
				{
				  "username": "member_test",
				  "password": "0000"
				}
				""";

		MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/member/auth/login")
				.contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Cookie tokenCookie = loginResult.getResponse().getCookie("accessToken");
		assertNotNull(tokenCookie);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/member/auth/me").cookie(tokenCookie))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockMember(username = "member_mock", roles = { "ADMIN" })
	void meEndpointTest() throws Exception {
		Member member = memberRepository.findByUsername("member_mock");

		assertNotNull(member);
		assertNotNull(member.getId());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/member/auth/me"))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockMember(username = "member_mock", roles = { "EDITOR" })
	void editorToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/member/get/all"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

}
