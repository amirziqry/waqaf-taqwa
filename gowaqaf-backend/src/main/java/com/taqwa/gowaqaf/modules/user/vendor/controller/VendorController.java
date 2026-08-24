package com.taqwa.gowaqaf.modules.user.vendor.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.user.vendor.dto.VendorRegisterCredentials;
import com.taqwa.gowaqaf.modules.user.vendor.dto.VendorRegisterResponse;
import com.taqwa.gowaqaf.modules.user.vendor.service.VendorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class VendorController {

	private final VendorService vendorService;

	@PostMapping("/register")
	public ResponseEntity<VendorRegisterResponse> register(@RequestBody VendorRegisterCredentials request) {
		VendorRegisterResponse response = vendorService.createDonator(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
