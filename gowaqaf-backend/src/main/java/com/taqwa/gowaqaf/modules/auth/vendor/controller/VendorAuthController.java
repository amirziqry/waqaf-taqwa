package com.taqwa.gowaqaf.modules.auth.vendor.controller;

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
import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorAuthDetails;
import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.vendor.dto.VendorLoginResponse;
import com.taqwa.gowaqaf.modules.auth.vendor.service.VendorAuthService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor/auth")
@RequiredArgsConstructor
public class VendorAuthController {

	private final VendorAuthService authService;
	private final AuthCookieManager authCookieManager;

	@PostMapping("/login")
	public ResponseEntity<VendorLoginResponse> login(@RequestBody VendorLoginCredentials request,
			HttpServletResponse response) {
		VendorAuthDetails auth = authService.login(request);

		ResponseCookie cookie = authCookieManager.createAccessTokenCookie(auth.getToken(), 60 * 15);

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		VendorLoginResponse dto = new VendorLoginResponse(auth.getUsername(), auth.getEmail());

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/me")
	@PreAuthorize("@accountSecurity.isVendor(authentication)")
	public ResponseEntity<?> me(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
