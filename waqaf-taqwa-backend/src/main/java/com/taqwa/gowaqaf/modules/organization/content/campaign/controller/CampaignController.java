package com.taqwa.gowaqaf.modules.organization.content.campaign.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.content.campaign.component.image.dto.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadRequest;
import com.taqwa.gowaqaf.modules.organization.content.campaign.dto.CampaignUploadResponse;
import com.taqwa.gowaqaf.modules.organization.content.campaign.service.CampaignService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/campaigns")
@RequiredArgsConstructor
public class CampaignController {

	private final CampaignService campaignService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<CampaignUploadResponse> createCampaign(@RequestBody CampaignUploadRequest request) {
		CampaignUploadResponse response = campaignService.createCampaign(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<CampaignUploadResponse> updateCampaignById(@PathVariable UUID id,
			@RequestBody CampaignUploadRequest request) {
		CampaignUploadResponse response = campaignService.updateCampaignById(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{id}/image-keys")
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<Void> updateCampaignImageKeysById(@PathVariable UUID id,
			@RequestBody List<CampaignImageKey> request) {
		campaignService.updateCampaignImageKeysById(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CampaignDetails> getCampaignDetailsById(@PathVariable UUID id) {
		CampaignDetails response = campaignService.getCampaignDetailsById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Page<CampaignDetails>> getAllCampaignDetails(
			@PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<CampaignDetails> response = campaignService.getAllCampaigns(pageable);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
	public ResponseEntity<Void> deleteCampaignById(@PathVariable UUID id) {
		campaignService.deleteCampaignById(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
