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
import com.taqwa.gowaqaf.modules.user.merchant.entity.Merchant;
import com.taqwa.gowaqaf.modules.user.merchant.repository.MerchantRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class MerchantAuthFlowTest {

	private final MockMvc mockMvc;
	private final AccountInfoRepository accountRepository;
	private final MerchantRepository merchantRepository;
	private final PasswordEncoder passwordEncoder;

	public static String registerEndpoint = "/api/merchant/register";
	public static String loginEndpoint = "/api/merchant/auth/login";
	public static String meEndpoint = "/api/merchant/auth/me";

	@BeforeEach
	void setup() {
		CommonClass.createMockMerchant(merchantRepository, accountRepository, passwordEncoder, "vendor_test",
				"test@gmail.com");

	}

	@Test
	void vendorRegisterShouldSuccess() throws Exception {

		String jsonPost1 = """
					{
				        "username": "vendor2",
				        "email": "-",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post(registerEndpoint).contentType(MediaType.APPLICATION_JSON)
				.content(jsonPost1)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("vendor2"));

		String jsonPost2 = """
						{
				        "username": "vendor2",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(
				MockMvcRequestBuilders.post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(jsonPost2))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("vendor2"));
	}

	@Test
	void vendorLoginShouldSuccess() throws Exception {
		String requestBody = """
						{
				        "username": "vendor_test",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("vendor_test"));
	}

	@Test
	void vendorInvalidPasswordShouldFail() throws Exception {
		String requestBody = """
						{
				        "username": "vendor_test",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void vendorCanAccessEndpointAfterLogin() throws Exception {
		String requestBody = """
				{
				  "username": "vendor_test",
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
	@WithMockMerchant(username = "vendor_mock")
	void meEndpointTest() throws Exception {
		Merchant merchant = merchantRepository.findByUsername("vendor_mock").get();

		assertNotNull(merchant);
		assertNotNull(merchant.getId());

		mockMvc.perform(get(meEndpoint)).andExpect(status().isOk());
	}

	////////////////////////////////
	// Cross account authentication test.
	////////////////////////////////

	@Test
	void adminToMerchantLoginShouldFail() throws Exception {
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
	void personalToMerchantLoginShouldFail() throws Exception {
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
	@WithMockAdmin(username = "member_mock", roles = { "ADMIN" })
	void adminToMerchantEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(meEndpoint)).andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void personalToMerchantEndpointShouldFail() throws Exception {
		mockMvc.perform(get(meEndpoint)).andExpect(status().isForbidden());
	}

}
