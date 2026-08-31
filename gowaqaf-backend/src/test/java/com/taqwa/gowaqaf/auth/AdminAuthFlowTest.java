package com.taqwa.gowaqaf.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.common.CommonClass;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.merchant.WithMockMerchant;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.enums.Role;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class AdminAuthFlowTest {

	private final MockMvc mockMvc;
	private final AdminRepository adminRepository;
	private final AccountInfoRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	public static String registerAdminEndpoint = "/api/admin/register-admin";
	public static String registerEditorEndpoint = "/api/admin/register-editor";
	public static String loginEndpoint = "/api/admin/auth/login";
	public static String meEndpoint = "/api/admin/auth/me";

	@BeforeEach
	void setup() {
		CommonClass.createMockAdmin(adminRepository, accountRepository, passwordEncoder, "member_test",
				"member1@gmail.com", Set.of(Role.ADMIN));
	}

	@Test
	void memberRegisterAdminShouldSuccess() throws Exception {

		String jsonPost1 = """
					{
				        "username": "member1",
				        "email": "-",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(registerAdminEndpoint).contentType(MediaType.APPLICATION_JSON).content(jsonPost1))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member1"));

		String jsonPost2 = """
						{
				        "username": "member1",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(jsonPost2))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member1"))
				.andExpect(jsonPath("$.roles", Matchers.hasItem("ROLE_ADMIN")));
	}

	@Test
	void memberRegisterEditorShouldSuccess() throws Exception {

		String jsonPost1 = """
					{
				        "username": "member2",
				        "email": "-",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(post(registerEditorEndpoint).contentType(MediaType.APPLICATION_JSON).content(jsonPost1))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member2"));

		String jsonPost2 = """
						{
				        "username": "member2",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(jsonPost2))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member2"))
				.andExpect(jsonPath("$.roles", Matchers.hasItem("ROLE_EDITOR")))
				.andExpect(jsonPath("$.roles", Matchers.not(Matchers.hasItem("ROLE_ADMIN"))));
	}

	@Test
	void memberLoginShouldSuccess() throws Exception {
		String requestBody = """
						{
				        "username": "member_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member_test"))
				.andExpect(jsonPath("$.roles", Matchers.hasItem("ROLE_ADMIN")));
	}

	@Test
	void memberInvalidPasswordShouldFail() throws Exception {
		String requestBody = """
						{
				        "username": "member_test",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	void memberCanAccessEndpointAfterLogin() throws Exception {
		String requestBody = """
				{
				  "username": "member_test",
				  "password": "0000"
				}
				""";

		MvcResult loginResult = mockMvc
				.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Cookie tokenCookie = loginResult.getResponse().getCookie("accessToken");
		assertNotNull(tokenCookie);

		mockMvc.perform(MockMvcRequestBuilders.get(meEndpoint).cookie(tokenCookie))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "ADMIN" })
	void meEndpointTest() throws Exception {
		Admin admin = adminRepository.findByUsername("member_mock").get();

		assertNotNull(admin);
		assertNotNull(admin.getId());

		mockMvc.perform(get(meEndpoint)).andExpect(status().isOk());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "EDITOR" })
	void editorToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(get("/api/admin/get/all")).andExpect(status().isForbidden());
	}

	////////////////////////////////
	// Cross account authentication test.
	////////////////////////////////

	@Test
	void merchantToAdminLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "vendor_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void personalToAdminLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "donator_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockMerchant(username = "vendor_mock")
	void merchantToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(get(meEndpoint)).andExpect(status().isForbidden());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void personalToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(get(meEndpoint)).andExpect(status().isForbidden());
	}

}
