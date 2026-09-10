package com.taqwa.gowaqaf.modules.user.merchant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.admin.dto.ChangePasswordRequest;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantAccountInfo;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;
import com.taqwa.gowaqaf.modules.user.personal.dto.AccountUploadFields;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.MERCHANT)
@RequiredArgsConstructor
public class MerchantController {

	private final MerchantService service;

	/**
	 * Merchant-only to register account.
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/register")
	public ResponseEntity<MerchantRegisterResponse> register(@RequestBody MerchantRegisterCredentials request) {
		MerchantRegisterResponse response = service.createMerchant(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Merchant-only to update account info.
	 * 
	 * @param authentication
	 * @param request
	 * @return
	 */
	@PutMapping("/account/update")
	@PreAuthorize("@accountSecurity.isMerchant(authentication)")
	public ResponseEntity<Void> updateAccountByUser(Authentication authentication,
			@RequestBody AccountUploadFields request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		service.updateAccountByUser(principal, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Merchant-only to change password.
	 * 
	 * @param authentication
	 * @param request
	 * @return
	 */
	@PutMapping("/account/password/change")
	@PreAuthorize("@accountSecurity.isMerchant(authentication)")
	public ResponseEntity<Void> changePassword(Authentication authentication,
			@RequestBody ChangePasswordRequest request) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		service.changePasswordByUsername(principal, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Merchant-only to get account info.
	 * 
	 * @param authentication
	 * @return
	 */
	@GetMapping("/account/get")
	@PreAuthorize("@accountSecurity.isMerchant(authentication)")
	public ResponseEntity<MerchantAccountInfo> getAccountByUser(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		MerchantAccountInfo response = service.getAccountByUser(principal);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
