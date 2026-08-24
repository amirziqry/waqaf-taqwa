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

import com.taqwa.gowaqaf.mockuser.donator.WithMockDonator;
import com.taqwa.gowaqaf.mockuser.member.WithMockMember;
import com.taqwa.gowaqaf.mockuser.vendor.WithMockVendor;
import com.taqwa.gowaqaf.modules.user.donator.entity.Donator;
import com.taqwa.gowaqaf.modules.user.donator.repository.DonatorRepository;
import com.taqwa.gowaqaf.modules.user.member.entity.Member;
import com.taqwa.gowaqaf.modules.user.member.repository.MemberRepository;
import com.taqwa.gowaqaf.modules.user.vendor.entity.Vendor;
import com.taqwa.gowaqaf.modules.user.vendor.repository.VendorRepository;

import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class CrossAccountSecurityTest {

	private final MockMvc mockMvc;
	private final MemberRepository memberRepository;
	private final DonatorRepository donatorRepository;
	private final VendorRepository vendorRepository;
	private final PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		Donator donator = new Donator();
		donator.setUsername("donator_test");
		donator.setPassword(passwordEncoder.encode("0000"));

		Vendor vendor = new Vendor();
		vendor.setUsername("vendor_test");
		vendor.setPassword(passwordEncoder.encode("0000"));

		Member member = new Member();
		member.setUsername("member_test");
		member.setPassword(passwordEncoder.encode("0000"));

		donatorRepository.save(donator);
		vendorRepository.save(vendor);
		memberRepository.save(member);
	}

	@Test
	void donatorToMemberLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "donator_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post("/api/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void donatorToVendorLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "donator_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post("/api/vendor/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void vendorToDonatorLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "vendor_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post("/api/donator/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void vendorToMemberLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "vendor_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post("/api/member/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void memberToDonatorLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "member_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post("/api/donator/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void memberToVendorLoginShouldFail() throws Exception {
		String requestBody = """
					{
				        "username": "member_test",
				        "email": "-",
				        "password": "0000"
				    }
				""";

		mockMvc.perform(post("/api/vendor/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockDonator(username = "donator_mock")
	void donatorToMemberEndpointShouldFail() throws Exception {
		mockMvc.perform(get("/api/member/auth/me")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockDonator(username = "donator_mock")
	void donatorToVendorEndpointShouldFail() throws Exception {
		mockMvc.perform(get("/api/vendor/auth/me")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockMember(username = "member_mock", roles = { "ADMIN" })
	void memberToDonatorEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/donator/auth/me"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockMember(username = "member_mock", roles = { "ADMIN" })
	void memberToVendorEndpointShouldFail() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/vendor/auth/me"))
				.andExpect(MockMvcResultMatchers.status().isForbidden());
	}

	@Test
	@WithMockVendor(username = "vendor_mock")
	void vendorToMemberEndpointShouldFail() throws Exception {
		mockMvc.perform(get("/api/member/auth/me")).andExpect(status().isForbidden());
	}

	@Test
	@WithMockVendor(username = "vendor_mock")
	void vendorToDonatorEndpointShouldFail() throws Exception {
		mockMvc.perform(get("/api/donator/auth/me")).andExpect(status().isForbidden());
	}

}
