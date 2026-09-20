package com.taqwa.gowaqaf.module.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.taqwa.gowaqaf.common.CommonEndpoints;
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.user.entity.Account;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.modules.user.repository.AccountRepository;

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
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	Account test;

	@BeforeEach
	void setup() {
		this.test = CommonClass.createMockAdmin(accountRepository, passwordEncoder, "member_test", "member1@gmail.com",
				Role.ADMIN);
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

		mockMvc.perform(
				post(CommonEndpoints.adminRegisterAdmin).contentType(MediaType.APPLICATION_JSON).content(jsonPost1))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("member1"));

		String jsonPost2 = """
						{
				        "username": "member1",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(jsonPost2))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member1"))
				.andExpect(jsonPath("$.role").value("ADMIN"));
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

		mockMvc.perform(
				post(CommonEndpoints.adminRegisterEditor).contentType(MediaType.APPLICATION_JSON).content(jsonPost1))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.username").value("member2"));

		String jsonPost2 = """
						{
				        "username": "member2",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(jsonPost2))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member2"))
				.andExpect(jsonPath("$.role").value("EDITOR"));
	}

	@Test
	void memberLoginShouldSuccess() throws Exception {
		String requestBody = """
						{
				        "username": "member_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("member_test"))
				.andExpect(jsonPath("$.role").value("ADMIN"));
	}

	@Test
	void memberInvalidPasswordShouldFail() throws Exception {
		String requestBody = """
						{
				        "username": "member_test",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(requestBody))
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
				.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		Cookie tokenCookie = loginResult.getResponse().getCookie("accessToken");
		assertNotNull(tokenCookie);

		mockMvc.perform(MockMvcRequestBuilders.get(CommonEndpoints.accountMe).cookie(tokenCookie))
				.andExpect(MockMvcResultMatchers.status().isOk());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "ADMIN" })
	void meEndpointTest() throws Exception {
		Account accountInfo = accountRepository.findByUsername("member_mock").get();

		assertNotNull(accountInfo);
		assertNotNull(accountInfo.getId());

		mockMvc.perform(get(CommonEndpoints.accountMe)).andExpect(status().isOk());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "EDITOR" })
	void editorToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(delete(CommonEndpoints.adminDelete, test.getId().toString())).andExpect(status().isForbidden());
	}

	////////////////////
	// Cross account authentication test.
	////////////////////

	@Test
	void merchantToAdminLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "vendor_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void personalToAdminLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "donator_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(CommonEndpoints.adminLogin).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void personalToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(get("/api/organization/donation/collections")).andExpect(status().isForbidden());
	}

}
