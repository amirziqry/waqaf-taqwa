package com.taqwa.gowaqaf.modules.organization.collection.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSum;
import com.taqwa.gowaqaf.modules.organization.collection.dto.OrganizationCollectionSumFilter;
import com.taqwa.gowaqaf.modules.organization.collection.service.OrganizationCollectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/collection")
@RequiredArgsConstructor
public class OrganizationCollectionController {

	private final OrganizationCollectionService organizationCollectionService;

	@GetMapping("/sum")
	@PreAuthorize("@accountSecurity.isAdmin(authentication) && hasRole('ADMIN')")
	public ResponseEntity<OrganizationCollectionSum> getCollectionSum(
			@ModelAttribute OrganizationCollectionSumFilter request) {
		OrganizationCollectionSum response = organizationCollectionService.getAllCollectionSum(request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
