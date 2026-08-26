package com.taqwa.gowaqaf.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.mockuser.donator.WithMockPersonal;
import com.taqwa.gowaqaf.mockuser.member.WithMockAdmin;
import com.taqwa.gowaqaf.mockuser.vendor.WithMockMerchant;
import com.taqwa.gowaqaf.modules.user.admin.entity.Admin;
import com.taqwa.gowaqaf.modules.user.admin.repository.AdminRepository;
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
public class CrossAccountSecurityTest {

	private final MockMvc mockMvc;
	private final AdminRepository adminRepository;
	private final PersonalRepository personalRepository;
	private final MerchantRepository merchantRepository;
	private final PasswordEncoder passwordEncoder;

	public static String adminLoginEndpoint = "/api/admin/auth/login";
	public static String merchantLoginEndpoint = "/api/merchant/auth/login";
	public static String personalLoginEndpoint = "/api/personal/auth/login";
	public static String adminMeEndpoint = "/api/admin/auth/me";
	public static String merchantMeEndpoint = "/api/merchant/auth/me";
	public static String personalMeEndpoint = "/api/personal/auth/me";

	@BeforeEach
	void setup() {
		Personal personal = new Personal();
		personal.setUsername("donator_test");
		personal.setPassword(passwordEncoder.encode("0000"));

		Merchant merchant = new Merchant();
		merchant.setUsername("vendor_test");
		merchant.setPassword(passwordEncoder.encode("0000"));

		Admin admin = new Admin();
		admin.setUsername("member_test");
		admin.setPassword(passwordEncoder.encode("0000"));

		personalRepository.save(personal);
		merchantRepository.save(merchant);
		adminRepository.save(admin);
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

		mockMvc.perform(post(adminLoginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
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

		mockMvc.perform(post(merchantLoginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void merchantToPersonalLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "vendor_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(personalLoginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void merchantToAdminLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "vendor_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(adminLoginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
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

		mockMvc.perform(post(personalLoginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminToMerchantLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "member_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post(merchantLoginEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void personalToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(get(adminMeEndpoint)).andExpect(status().isForbidden());
	}

	@Test
	@WithMockPersonal(username = "donator_mock")
	void personalToMerchantEndpointShouldFail() throws Exception {
		mockMvc.perform(get(merchantMeEndpoint)).andExpect(status().isForbidden());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "ADMIN" })
	void adminToPersonalEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(personalMeEndpoint))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockAdmin(username = "member_mock", roles = { "ADMIN" })
	void adminToMerchantEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(merchantMeEndpoint))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockMerchant(username = "vendor_mock")
	void merchantToAdminEndpointShouldFail() throws Exception {
		mockMvc.perform(get(adminMeEndpoint)).andExpect(status().isForbidden());
	}

	@Test
	@WithMockMerchant(username = "vendor_mock")
	void merchantToPersonalEndpointShouldFail() throws Exception {
		mockMvc.perform(get(personalMeEndpoint)).andExpect(status().isForbidden());
	}

}
