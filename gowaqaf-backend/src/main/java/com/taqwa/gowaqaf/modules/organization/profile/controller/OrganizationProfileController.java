package com.taqwa.gowaqaf.modules.organization.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationImagesRequest;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileDetails;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUpload;
import com.taqwa.gowaqaf.modules.organization.profile.dto.OrganizationProfileUploadUrlsResponse;
import com.taqwa.gowaqaf.modules.organization.profile.service.OrganizationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/profile")
@RequiredArgsConstructor
public class OrganizationProfileController {

	private final OrganizationService organizationService;

	@PutMapping("/update-request")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<OrganizationProfileUploadUrlsResponse> updateProfile(@RequestBody OrganizationProfileUpload dto) {
		OrganizationProfileUploadUrlsResponse response = organizationService.updateProfile(dto);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/image-keys/upload")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<Void> updateImageKeys(@RequestBody OrganizationImagesRequest request) {
		organizationService.uploadImageKeys(request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/get")
	public ResponseEntity<OrganizationProfileDetails> getProfile() {
		OrganizationProfileDetails dto = organizationService.getProfile();

		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

}
