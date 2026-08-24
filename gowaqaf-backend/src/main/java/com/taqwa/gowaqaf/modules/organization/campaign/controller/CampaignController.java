package com.taqwa.gowaqaf.modules.organization.campaign.controller;

import java.util.List;
import java.util.UUID;

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

import com.taqwa.gowaqaf.modules.organization.campaign.component.image.CampaignImageKey;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignDetails;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.dto.CampaignUploadResponse;
import com.taqwa.gowaqaf.modules.organization.campaign.service.CampaignService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/campaign")
@RequiredArgsConstructor
public class CampaignController {

	private final CampaignService campaignService;

	@PostMapping("/create")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<CampaignUploadResponse> createCampaign(@RequestBody CampaignUploadRequest request) {
		CampaignUploadResponse response = campaignService.createCampaign(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<CampaignUploadResponse> updateCampaignById(@PathVariable UUID id,
			@RequestBody CampaignUploadRequest request) {
		CampaignUploadResponse response = campaignService.updateCampaignById(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{id}/image-keys/upload")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<Void> updateCampaignImageKeysById(@PathVariable UUID id,
			@RequestBody List<CampaignImageKey> request) {
		campaignService.updateCampaignImageKeysById(id, request);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<CampaignDetails> getCampaignDetailsById(@PathVariable UUID id) {
		CampaignDetails response = campaignService.getCampaignDetailsById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<CampaignDetails>> getAllCampaignDetails() {
		List<CampaignDetails> response = campaignService.getAllCampaigns();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	@PreAuthorize("@accountSecurity.isMember(authentication)")
	public ResponseEntity<Void> deleteCampaignById(@PathVariable UUID id) {
		campaignService.deleteCampaignById(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
