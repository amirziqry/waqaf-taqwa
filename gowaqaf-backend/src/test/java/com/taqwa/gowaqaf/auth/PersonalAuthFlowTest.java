package com.taqwa.gowaqaf.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.taqwa.gowaqaf.mockuser.admin.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.merchant.WithMockMerchant;
import com.taqwa.gowaqaf.mockuser.personal.WithMockPersonal;
import com.taqwa.gowaqaf.modules.user.account.repository.AccountInfoRepository;
import com.taqwa.gowaqaf.modules.user.personal.entity.Personal;
import com.taqwa.gowaqaf.modules.user.personal.repository.PersonalRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class PersonalAuthFlowTest {

	private final MockMvc mockMvc;
	private final AccountInfoRepository acountRepository;
	private final PersonalRepository personalRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		CommonClass.createMockPersonal(personalRepository, acountRepository, passwordEncoder, "donator_test",
				"test@gmail.com");
	}

	public static String loginEndpoint = "/api/personal/auth/login";
	public static String registerEndpoint = "/api/personal/register";
	public static String meEndpoint = "/api/personal/auth/me";

	@Test
	void donatorRegisterShouldSuccess() throws Exception {
		String requestBody = """
					{
				        "username": "donator2",
				        "email": "-",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(registerEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("donator2"));

		requestBody = """
						{
				        "username": "donator2",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("donator2"));
	}

	@Test
	void donatorLoginShouldSuccess() throws Exception {
		String requestBody = """
						{
				        "username": "donator_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("donator_test"));
	}

	@Test
	void donatorInvalidPasswordShouldFail() throws Exception {
		String requestBody = """
						{
				        "username": "donator_test",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void donatorCanAccessEndpointAfterLogin() throws Exception {
		String requestBody = """
				{
				  "username": "donator_test",
				  "password": "0000"
				}
				""";

		MvcResult response = mockMvc
				.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

		Cookie tokenCookie = response.getResponse().getCookie("accessToken");
		assertNotNull(tokenCookie);

		mockMvc.perform(get(meEndpoint).cookie(tokenCookie)).andExpect(status().isOk());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void meEndpointTest() throws Exception {
		Personal personal = personalRepository.findByUsername("donator_mock").get();

		assertNotNull(personal);
		assertNotNull(personal.getId());

		mockMvc.perform(get(meEndpoint)).andExpect(status().isOk());
	}

	//////////////////////////////
	// Cross account authentication test.
	//////////////////////////////

	@Test
	void merchantToPersonalLoginShouldFail() throws Exception {
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
	void adminToPersonalLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "member_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "ADMIN" })
	void adminToPersonalEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(meEndpoint)).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockMerchant(username = "vendor_mock")
	void merchantToPersonalEndpointShouldFail() throws Exception {
		mockMvc.perform(get(meEndpoint)).andExpect(status().isForbidden());
	}

}
