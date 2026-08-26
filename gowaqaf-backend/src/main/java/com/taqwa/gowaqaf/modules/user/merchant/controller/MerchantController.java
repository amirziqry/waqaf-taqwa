package com.taqwa.gowaqaf.modules.user.merchant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.merchant.dto.MerchantRegisterResponse;
import com.taqwa.gowaqaf.modules.user.merchant.service.MerchantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.MERCHANT)
@RequiredArgsConstructor
public class MerchantController {

	private final MerchantService merchantService;

	@PostMapping("/register")
	public ResponseEntity<MerchantRegisterResponse> register(@RequestBody MerchantRegisterCredentials request) {
		MerchantRegisterResponse response = merchantService.createMerchant(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
