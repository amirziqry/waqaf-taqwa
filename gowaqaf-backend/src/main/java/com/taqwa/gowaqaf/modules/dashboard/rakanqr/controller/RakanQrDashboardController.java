package com.taqwa.gowaqaf.modules.dashboard.rakanqr.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.dashboard.rakanqr.dto.RakanQrDashboard;
import com.taqwa.gowaqaf.modules.dashboard.rakanqr.service.RakanQrDashboardService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * <p>
 * RakanQr agents dashboard handler.
 * <p>
 */
@RestController
@RequestMapping(ApiBasePath.RAKANQR)
@RequiredArgsConstructor
public class RakanQrDashboardController {

	private final RakanQrDashboardService service;

	/**
	 * FINAL
	 * Personal/Merchant only - Respective RakanQr dashboard.
	 * 
	 * @param authentication
	 * @return
	 */
	@GetMapping("/dashboard")
	@PreAuthorize("@accountSecurity.isPersonal(authentication) || @accountSecurity.isMerchant(authentication)")
	public ResponseEntity<RakanQrDashboard> getDashboardByUser(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		RakanQrDashboard response = service.getDashboardByUser(principal);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
