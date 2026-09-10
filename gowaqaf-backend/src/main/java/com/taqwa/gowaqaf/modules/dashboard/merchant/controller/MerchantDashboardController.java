package com.taqwa.gowaqaf.modules.dashboard.merchant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.dashboard.merchant.dto.MerchantDashboard;
import com.taqwa.gowaqaf.modules.dashboard.merchant.service.MerchantDashboardService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.MERCHANT + "/dashboard")
@RequiredArgsConstructor
public class MerchantDashboardController {

	private final MerchantDashboardService dashboardService;

	@GetMapping("/get")
	@PreAuthorize("@accountSecurity.isMerchant(authentication)")
	public ResponseEntity<MerchantDashboard> getDashboardByUser(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();
		if (principal == null)
			throw new UsernameNotFoundException("Invalid Username or Password");

		MerchantDashboard response = dashboardService.getDashboardByUser(principal);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
