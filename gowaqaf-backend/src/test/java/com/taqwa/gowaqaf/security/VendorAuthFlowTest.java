package com.taqwa.gowaqaf.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.taqwa.gowaqaf.mockuser.vendor.WithMockVendor;
import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class VendorAuthFlowTest {

	private final MockMvc mockMvc;
	private final VendorRepository vendorRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Vendor vendor = new Vendor();

		vendor.setUsername("vendor_test");
		vendor.setPassword(passwordEncoder.encode("0000"));

		vendorRepository.save(vendor);
	}

	@Disabled
	@Test
	void vendorRegisterShouldSuccess() throws Exception {

		String jsonPost1 = """
					{
				        "username": "vendor2",
				        "email": "-",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/vendor/register").contentType(MediaType.APPLICATION_JSON)
				.content(jsonPost1)).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value("vendor2"));

		String jsonPost2 = """
						{
				        "username": "vendor2",
				        "password": "2222"
				    }
				""";

		mockMvc.perform(MockMvcRequestBuilders.post("/api/vendor/auth/login").contentType(MediaType.APPLICATION_JSON)
				.content(jsonPost2)).andExpect(MockMvcResultMatchers.status().isOk())
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

		mockMvc.perform(post("/api/vendor/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
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

		mockMvc.perform(post("/api/vendor/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
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
				.perform(post("/api/vendor/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

		Cookie tokenCookie = response.getResponse().getCookie("accessToken");
		assertNotNull(tokenCookie);

		mockMvc.perform(get("/api/vendor/auth/me").cookie(tokenCookie)).andExpect(status().isOk());
	}

	@Test
	@WithMockVendor(username = "vendor_mock")
	void meEndpointTest() throws Exception {
		Vendor vendor = vendorRepository.findByUsername("vendor_mock");

		assertNotNull(vendor);
		assertNotNull(vendor.getId());

		mockMvc.perform(get("/api/vendor/auth/me")).andExpect(status().isOk());
	}

}
