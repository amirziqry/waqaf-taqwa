package com.taqwa.gowaqaf.security;

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
import org.springframework.transaction.annotation.Transactional;

import com.taqwa.gowaqaf.mockuser.donator.WithMockDonator;
import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class DonatorAuthFlowTest {

	private final MockMvc mockMvc;
	private final DonatorRepository donatorRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Donator donator = new Donator();

		donator.setUsername("donator_test");
		donator.setPassword(passwordEncoder.encode("0000"));

		donatorRepository.save(donator);
	}

	@Test
	void donatorRegisterShouldSuccess() throws Exception {
		String requestBody = """
					{
				        "username": "donator2",
				        "email": "-",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post("/api/donator/register").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andExpect(jsonPath("$.username").value("donator2"));

		requestBody = """
						{
				        "username": "donator2",
				        "password": "1111"
				    }
				""";

		mockMvc.perform(post("/api/donator/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
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

		mockMvc.perform(post("/api/donator/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
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

		mockMvc.perform(post("/api/donator/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
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
				.perform(post("/api/donator/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

		Cookie tokenCookie = response.getResponse().getCookie("accessToken");
		assertNotNull(tokenCookie);

		mockMvc.perform(get("/api/donator/auth/me").cookie(tokenCookie)).andExpect(status().isOk());
	}

	@Test
	@WithMockDonator(username = "donator_mock")
	void meEndpointTest() throws Exception {
		Donator donator = donatorRepository.findByUsername("donator_mock");

		assertNotNull(donator);
		assertNotNull(donator.getId());

		mockMvc.perform(get("/api/donator/auth/me")).andExpect(status().isOk());
	}

}
