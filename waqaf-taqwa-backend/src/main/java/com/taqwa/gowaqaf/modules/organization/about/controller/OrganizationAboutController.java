package com.taqwa.gowaqaf.modules.organization.about.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.about.dto.OrgAboutDetails;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationAboutUpload;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationAboutUploadUrlsResponse;
import com.taqwa.gowaqaf.modules.organization.about.dto.OrganizationImagesRequest;
import com.taqwa.gowaqaf.modules.organization.about.service.OrganizationAboutService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/about")
@RequiredArgsConstructor
public class OrganizationAboutController {

	private final OrganizationAboutService service;

	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<OrganizationAboutUploadUrlsResponse> updateAbout(@RequestBody OrganizationAboutUpload dto) {
		OrganizationAboutUploadUrlsResponse response = service.updateAbout(dto);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/image-keys")
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<Void> updateImageKeys(@RequestBody OrganizationImagesRequest request) {
		service.uploadImageKeys(request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<OrgAboutDetails> getAbout() {
		OrgAboutDetails dto = service.getAbout();

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

}
