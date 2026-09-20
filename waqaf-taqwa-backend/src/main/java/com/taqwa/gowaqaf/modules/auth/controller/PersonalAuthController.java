package com.taqwa.gowaqaf.modules.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.auth.cookie.AuthCookieManager;
import com.taqwa.gowaqaf.modules.auth.dto.AuthDetails;
import com.taqwa.gowaqaf.modules.auth.dto.AuthResponse;
import com.taqwa.gowaqaf.modules.auth.dto.LoginCredentials;
import com.taqwa.gowaqaf.modules.auth.service.PersonalAuthService;
import com.taqwa.gowaqaf.modules.user.enums.Role;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.USER + "/auth")
@RequiredArgsConstructor
public class PersonalAuthController {

	private final PersonalAuthService authService;
	private final AuthCookieManager authCookieManager;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginCredentials request, HttpServletResponse response) {
		AuthDetails auth = authService.login(request);

		ResponseCookie cookie = authCookieManager.createAccessTokenCookie(auth.getToken(), 60 * 15);

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		Role role = auth.getRoles().stream().findFirst().map(r -> Role.valueOf(r.replace("ROLE_", ""))).orElse(null);

		return new ResponseEntity<>(new AuthResponse(auth.getUsername(), role), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		ResponseCookie cookie = authCookieManager.clearAccessTokenCookie();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
