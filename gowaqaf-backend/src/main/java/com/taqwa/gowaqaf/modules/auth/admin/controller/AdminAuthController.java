package com.taqwa.gowaqaf.modules.auth.admin.controller;

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
import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminAuthDetails;
import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.admin.dto.AdminLoginResponse;
import com.taqwa.gowaqaf.modules.auth.admin.service.AdminAuthService;
import com.taqwa.gowaqaf.modules.auth.cookie.AuthCookieManager;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.ADMIN + "/auth")
@RequiredArgsConstructor
public class AdminAuthController {

	private final AdminAuthService authService;
	private final AuthCookieManager authCookieManager;

	@PostMapping("/login")
	public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginCredentials request,
			HttpServletResponse response) {
		AdminAuthDetails auth = authService.login(request);

		ResponseCookie cookie = authCookieManager.createAccessTokenCookie(auth.getToken(), 60 * 60 * 8);

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		AdminLoginResponse dto = new AdminLoginResponse(auth.getUsername(), auth.getEmail(), auth.getRoles());

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {

		ResponseCookie cookie = authCookieManager.clearAccessTokenCookie();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/me")
	@PreAuthorize("@accountSecurity.isAdmin(authentication)")
	public ResponseEntity<?> me(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
