package com.taqwa.gowaqaf.modules.user.personal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.personal.dto.AccountUploadFields;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalAccountInfo;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.personal.dto.PersonalRegisterResponse;
import com.taqwa.gowaqaf.modules.user.personal.service.PersonalService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.PERSONAL)
@RequiredArgsConstructor
public class PersonalController {

	private final PersonalService personalService;

	/**
	 * Personal account registration end-point.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/register")
	public ResponseEntity<PersonalRegisterResponse> register(@RequestBody PersonalRegisterCredentials request) {
		PersonalRegisterResponse response = personalService.createPersonal(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Personal account update end-point.
	 * 
	 * @param authentication
	 * @param request
	 * @return
	 */
	@PutMapping("/account/update")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<Void> updateAccountByUser(Authentication authentication,
			@RequestBody AccountUploadFields request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		personalService.updateAccountByUser(principal, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Personal account get end-point.
	 * 
	 * @param authentication
	 * @return
	 */
	@GetMapping("/account/get")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PersonalAccountInfo> getProfileByUser(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid username or password");

		PersonalAccountInfo response = personalService.getAccountByUser(principal);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
