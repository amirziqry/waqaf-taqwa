package com.taqwa.gowaqaf.module.flow;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Transactional
@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RequiredArgsConstructor
public class AdminEndToEndFlowTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MockMvc mockMvc;

	public static String username = "adminmock";
	public static String password = "0000";
	public static String email = "adminmock@gmail.com";
	public static String registerAdminEndpoint = "/api/admin/register-admin";
	public static String registerEditorEndpoint = "/api/admin/register-editor";
	public static String loginEndpoint = "/api/admin/auth/login";
	public static String logoutEndpoint = "/api/admin/auth/logout";
	public static String meEndpoint = "/api/admin/auth/me";

	private Cookie cookie;

	@Test
	void endToEndFlowTest() throws Exception {
		// Register, login, logout test;
		adminRegister(username, email, password);
		adminLogin(username, password);
		adminMe();
		adminLogout();
		adminMeFail();

		// External data input.
		editorRegister("editor1", "editor1@gmail.com", "000");
		editorRegister("editor2", "editor2@gmail.com", "000");

		// Continue test.
		adminLogin(username, password);
		List<String> usernames = getAdminList();
		adminUpdateRole(usernames.getFirst(), "ADMIN");
		getAdmin(usernames.getFirst(), "ADMIN");

	}

	private void adminRegister(String username, String email, String password) throws Exception {
		String requestBody = """
					{
				        "username": "%s",
				        "email": "%s",
				        "password": "%s"
				    }
				""".formatted(username, email, password);

		mockMvc.perform(post(registerAdminEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated());
	}

	private void editorRegister(String username, String email, String password) throws Exception {
		String requestBody = """
					{
				        "username": "%s",
				        "email": "%s",
				        "password": "%s"
				    }
				""".formatted(username, email, password);

		mockMvc.perform(post(registerEditorEndpoint).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isCreated());
	}

	private void adminLogin(String username, String password) throws Exception {
		String jsonPost2 = """
						{
				        "username": "%s",
				        "password": "%s"
				    }
				""".formatted(username, password);

		MvcResult response = mockMvc
				.perform(post(loginEndpoint).contentType(MediaType.APPLICATION_JSON).content(jsonPost2))
				.andExpect(status().isOk()).andReturn();

		this.cookie = response.getResponse().getCookie("accessToken");
	}

	private void adminLogout() throws Exception {
		mockMvc.perform(post(logoutEndpoint)).andExpect(status().isOk());

		this.cookie = null;
	}

	private void adminMe() throws Exception {
		mockMvc.perform(get(meEndpoint).cookie(this.cookie)).andExpect(status().isOk());
	}

	private void adminMeFail() throws Exception {
		mockMvc.perform(get(meEndpoint)).andExpect(status().isUnauthorized());
	}

	private List<String> getAdminList() throws Exception {
		MvcResult response = mockMvc.perform(get("/api/admin/get/all").cookie(this.cookie)).andExpect(status().isOk())
				.andReturn();

		String object = response.getResponse().getContentAsString();
		JsonNode json = objectMapper.readTree(object);

		List<String> usernames = new ArrayList<>();
		for (JsonNode admin : json)
			usernames.add(admin.get("username").asText());

		return usernames;
	}

	private void adminUpdateRole(String username, String role) throws Exception {
		String requestJson = """
				{
					"role": "%s"
				}
				""".formatted(role);

		mockMvc.perform(patch("/api/admin/update/" + username + "/role").contentType(MediaType.APPLICATION_JSON)
				.content(requestJson).cookie(this.cookie)).andExpect(MockMvcResultMatchers.status().isOk());
	}

	private void getAdmin(String username, String role) throws Exception {
		mockMvc.perform(get("/api/admin/get/" + username).cookie(this.cookie)).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value(username)).andExpect(jsonPath("$.roles").value(hasItem(role)));
	}

}
