package com.taqwa.gowaqaf.modules.dashboard.personal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.dashboard.personal.dto.PersonalDashboard;
import com.taqwa.gowaqaf.modules.dashboard.personal.service.PersonalDashboardService;
import com.taqwa.gowaqaf.security.principal.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.USER)
@RequiredArgsConstructor
public class PersonalDashboardController {

	private final PersonalDashboardService dashboardService;

	@GetMapping("/dashboard")
	public ResponseEntity<PersonalDashboard> getDashboardByUser(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

		PersonalDashboard response = dashboardService.getDashboardByUser(principal);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
