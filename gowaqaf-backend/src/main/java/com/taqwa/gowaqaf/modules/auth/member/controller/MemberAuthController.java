package com.taqwa.gowaqaf.modules.auth.member.controller;

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
import com.taqwa.gowaqaf.modules.auth.member.dto.MemberAuthDetails;
import com.taqwa.gowaqaf.modules.auth.member.dto.MemberLoginCredentials;
import com.taqwa.gowaqaf.modules.auth.member.dto.MemberLoginResponse;
import com.taqwa.gowaqaf.modules.auth.member.service.MemberAuthService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member/auth")
@RequiredArgsConstructor
public class MemberAuthController {

	private final MemberAuthService authService;
	private final AuthCookieManager authCookieManager;

	@PostMapping("/login")
	public ResponseEntity<MemberLoginResponse> login(@RequestBody MemberLoginCredentials request,
			HttpServletResponse response) {
		MemberAuthDetails auth = authService.login(request);

		ResponseCookie cookie = authCookieManager.createAccessTokenCookie(auth.getToken(), 60 * 60 * 8);

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		MemberLoginResponse dto = new MemberLoginResponse(auth.getUsername(), auth.getEmail(), auth.getRoles());

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@GetMapping("/me")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<?> me(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
