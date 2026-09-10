package com.taqwa.gowaqaf.modules.dashboard.personal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.dashboard.personal.dto.PersonalDashboard;
import com.taqwa.gowaqaf.modules.dashboard.personal.service.PersonalDashboardService;
import com.taqwa.gowaqaf.security.account.AccountUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.PERSONAL)
@RequiredArgsConstructor
public class PersonalDashboardController {

	private final PersonalDashboardService dashboardService;

	@GetMapping("/dashboard")
	@PreAuthorize("@accountSecurity.isPersonal(authentication)")
	public ResponseEntity<PersonalDashboard> getDashboardByUser(Authentication authentication) {
		AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

		PersonalDashboard response = dashboardService.getDashboardByUser(principal);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
