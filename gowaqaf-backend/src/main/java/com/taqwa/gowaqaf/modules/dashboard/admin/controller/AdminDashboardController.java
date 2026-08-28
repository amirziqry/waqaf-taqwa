package com.taqwa.gowaqaf.modules.dashboard.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.common.ApiBasePath;
import com.taqwa.gowaqaf.modules.dashboard.admin.dto.AdminDashboard;
import com.taqwa.gowaqaf.modules.dashboard.admin.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiBasePath.ADMIN + "/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

	private final AdminDashboardService dashboardService;

	@GetMapping("/get")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<AdminDashboard> getDashboard() {
		AdminDashboard response = dashboardService.getDashboard();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
