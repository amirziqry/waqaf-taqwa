package com.taqwa.gowaqaf.modules.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.dto.AccountInfo;
import com.taqwa.gowaqaf.modules.user.dto.AccountUploadFields;
import com.taqwa.gowaqaf.modules.user.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.dto.RegisterCredentials;
import com.taqwa.gowaqaf.modules.user.dto.RegisterResponse;
import com.taqwa.gowaqaf.modules.user.service.UserService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.USER)
@RequiredArgsConstructor
public class UserController {

	private final UserService service;

	/**
	 * Personal account registration end-point.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@RequestBody RegisterCredentials request) {
		RegisterResponse response = service.createUser(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * FINAL Personal account update end-point.
	 * 
	 * @param authentication
	 * @param request
	 * @return
	 */
	@PutMapping("/users/account")
	public ResponseEntity<Void> updateUserDetailsById(Authentication authentication,
			@RequestBody AccountUploadFields request) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		service.updateUserInfoById(principal.getId(), request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Change password needs email verification.
	 * 
	 * @param authentication
	 * @param request
	 * @return
	 */
	@PatchMapping("/users/account/password")
	public ResponseEntity<Void> changePassword(Authentication authentication,
			@RequestBody ChangePasswordRequest request) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		service.changeUserPassword(principal.getId(), request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * FINAL Personal account get end-point.
	 * 
	 * @param authentication
	 * @return
	 */
	@GetMapping("/users/account")
	public ResponseEntity<AccountInfo> getAccountByUser(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		AccountInfo response = service.getUserDetailsById(principal.getId());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
