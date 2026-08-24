package com.taqwa.gowaqaf.modules.auth.donator.controller;

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

import com.taqwa.gowaqaf.modules.auth.cookie.AuthCookieManager;
import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorAuthDetails;
import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.donator.dto.DonatorLoginResponse;
import com.taqwa.gowaqaf.modules.auth.donator.service.DonatorAuthService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/donator/auth")
@RequiredArgsConstructor
public class DonatorAuthController {

	private final DonatorAuthService authService;
	private final AuthCookieManager authCookieManager;

	@PostMapping("/login")
	public ResponseEntity<DonatorLoginResponse> login(@RequestBody DonatorLoginCredentials request,
			HttpServletResponse response) {
		DonatorAuthDetails auth = authService.login(request);

		ResponseCookie cookie = authCookieManager.createAccessTokenCookie(auth.getToken(), 60 * 15);

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		DonatorLoginResponse dto = new DonatorLoginResponse(auth.getUsername(), auth.getEmail());

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/me")
	@PreAuthorize("@accountSecurity.isDonator(authentication)")
	public ResponseEntity<?> me(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
