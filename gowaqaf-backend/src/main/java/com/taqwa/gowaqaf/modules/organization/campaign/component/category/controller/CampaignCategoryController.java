package com.taqwa.gowaqaf.modules.organization.campaign.component.category.controller;

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

import com.taqwa.gowaqaf.modules.organization.campaign.component.category.dto.CampaignCategoryDto;
import com.taqwa.gowaqaf.modules.organization.campaign.component.category.service.CampaignCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/organization/campaign/category")
@RequiredArgsConstructor
public class CampaignCategoryController {

	private final CampaignCategoryService service;

	@PostMapping("/create")
	public ResponseEntity<CampaignCategoryDto> createCategory(@RequestBody CampaignCategoryUploadRequest request) {
		CampaignCategoryDto response = service.createCategory(request);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}/update/")
	public ResponseEntity<CampaignCategoryDto> updateCategory(@PathVariable Long id,
			@RequestBody CampaignCategoryUploadRequest request) {
		CampaignCategoryDto response = service.updateCategory(id, request);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}/get")
	public ResponseEntity<CampaignCategoryDto> getCategory(@PathVariable Long id) {
		CampaignCategoryDto response = service.getCategoryDtoById(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/all/get")
	public ResponseEntity<List<CampaignCategoryDto>> getCategoryList() {
		List<CampaignCategoryDto> response = service.getCategoryDtoList();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		service.deleteCategory(id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
