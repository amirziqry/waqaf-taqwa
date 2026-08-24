package com.taqwa.gowaqaf.modules.donation.organization.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.donation.organization.dto.OrganizationDonationSum;
import com.taqwa.gowaqaf.modules.donation.organization.service.OrganizationDonationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/donation")
@RequiredArgsConstructor
public class OrganizationDonationController {

	private final OrganizationDonationService organizationDonationService;

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isMember(authentication) && hasRole('ADMIN')")
	public ResponseEntity<OrganizationDonationSum> getDonationSummary() {
		OrganizationDonationSum response = organizationDonationService.getAllDonationSum();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
