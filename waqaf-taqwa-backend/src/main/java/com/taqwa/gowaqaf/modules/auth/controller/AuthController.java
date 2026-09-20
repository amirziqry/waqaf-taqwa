package com.taqwa.gowaqaf.modules.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.auth.dto.AuthResponse;
import com.taqwa.gowaqaf.modules.user.enums.Role;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account/auth")
@RequiredArgsConstructor
public class AuthController {

	@GetMapping("/me")
	public ResponseEntity<AuthResponse> me(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		Role role = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.map(authority -> Role.valueOf(authority.replace("ROLE_", ""))).findFirst()
				.orElseThrow(() -> new IllegalStateException("Account has no role"));

		return new ResponseEntity<>(new AuthResponse(principal.getUsername(), role), HttpStatus.OK);
	}

}
