package com.taqwa.gowaqaf.modules.organization.campaign.component.tag.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.dto.CampaignTagUploadRequest;
import com.taqwa.gowaqaf.modules.organization.campaign.component.tag.service.CampaignTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/campaign/tag")
@RequiredArgsConstructor
public class CampaignTagController {

	private final CampaignTagService service;

	@PostMapping("/create")
	public ResponseEntity<CampaignTagDto> createTag(@RequestBody CampaignTagUploadRequest request) {
		CampaignTagDto response = service.createTag(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update")
	public ResponseEntity<CampaignTagDto> updateTag(@PathVariable Long id,
			@RequestBody CampaignTagUploadRequest request) {
		CampaignTagDto response = service.updateTag(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<CampaignTagDto> getTag(@PathVariable Long id) {
		CampaignTagDto response = service.getTagDtoById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<CampaignTagDto>> getTagList() {
		List<CampaignTagDto> response = service.getTagDtoList();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
		service.deleteTag(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
