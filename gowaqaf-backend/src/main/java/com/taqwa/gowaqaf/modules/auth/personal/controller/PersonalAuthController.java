package com.taqwa.gowaqaf.modules.auth.personal.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.auth.cookie.AuthCookieManager;
import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalAuthDetails;
import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.personal.dto.PersonalLoginResponse;
import com.taqwa.gowaqaf.modules.auth.personal.service.PersonalAuthService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.PERSONAL + "/auth")
@RequiredArgsConstructor
public class PersonalAuthController {

	private final PersonalAuthService authService;
	private final AuthCookieManager authCookieManager;

	@PostMapping("/login")
	public ResponseEntity<PersonalLoginResponse> login(@RequestBody PersonalLoginCredentials request,
			HttpServletResponse response) {
		PersonalAuthDetails auth = authService.login(request);

		ResponseCookie cookie = authCookieManager.createAccessTokenCookie(auth.getToken(), 60 * 15);

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		PersonalLoginResponse dto = new PersonalLoginResponse(auth.getUsername(), auth.getEmail());

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/me")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<?> me(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
